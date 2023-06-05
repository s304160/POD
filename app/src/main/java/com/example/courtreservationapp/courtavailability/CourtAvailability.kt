package com.example.courtreservationapp.courtavailability

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.courtreservationapp.LoadingAnimation2
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Info
import com.example.courtreservationapp.data.TimeSlot
import com.example.courtreservationapp.reservations.Day
import com.example.courtreservationapp.reservations.DaysOfWeekTitle
import com.example.courtreservationapp.ui.theme.calendarColors
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationPage() {
    val lang = Locale.current.language

    val viewModel = viewModel(CAViewModel::class.java)

    val loading = viewModel.loading.observeAsState()
    val sports = viewModel.sports.observeAsState()
    val sportId = viewModel.sportId.observeAsState()

    val (expanded, setExpanded) = remember {
        mutableStateOf(false)
    }

    if (loading.value == true)
        LoadingAnimation2()
    else
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.choose_a_date),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    },
                    actions = {
                        Text(
                            sports.value?.find {
                                it.id == sportId.value
                            }?.name?.get(lang).orEmpty(),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Box {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                "Dropdown Icon",
                                Modifier.clickable { setExpanded(!expanded) }
                            )

                            DropdownMenu(
                                expanded,
                                { setExpanded(!expanded) }
                            ) {
                                sports.value?.filter { it.id != sportId.value }?.map {
                                    DropdownMenuItem(
                                        { Text(
                                            it.name[lang]?: it.name["en"]!!,
                                            style = MaterialTheme.typography.titleLarge
                                        ) },
                                        {
                                            setExpanded(false)
                                            viewModel.setSportId(it.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) {
            CourtAvailability(Modifier.padding(it), sportId.value?: "", viewModel)
        }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CourtAvailability(modifier: Modifier = Modifier, sportId: String, viewModel: CAViewModel) {
    val (visible, setVisible) = remember { mutableStateOf(false) }
    val (date, setDate) = remember { mutableStateOf(LocalDate.MIN) }
    val (courtId, setCourtId) = remember { mutableStateOf("") }
    val (displayGrid, setDisplayGrid) = remember { mutableStateOf(false) }
    val (availableTs, setAvailableTs) = remember { mutableStateOf(mapOf<TimeSlot, List<Info>>()) }
    val (selectedTs, setSelectedTs) = remember { mutableStateOf(listOf<String>()) }
    val available = remember { mutableMapOf<String, Map<TimeSlot, List<Info>>>() }
    val courts = viewModel.courts.observeAsState()

    val context = LocalContext.current

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Calendar(
            Modifier,
            viewModel,
            date,
            { day ->
                val dateString = day.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                val availableTimeSlots = viewModel.addDate(dateString, sportId)

                available[dateString] = availableTimeSlots

                availableTimeSlots.filter { ts ->
                    ts.value.any { it.available }
                }.size
            },
            {
                val dateString = it.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                setDisplayGrid(true)
                if (it == date) setDisplayGrid(!displayGrid)
                setSelectedTs(listOf())
                setAvailableTs(available[dateString] ?: mapOf())
                setDate(it)
            }
        )

        AnimatedContent(
            modifier = Modifier.weight(1f, true),
            targetState = date,
            transitionSpec = {
                fadeIn() + slideInVertically(animationSpec = tween(500),
                    initialOffsetY = { fullHeight -> fullHeight }
                ) with fadeOut(tween(500))
            }
        ) {
            if (displayGrid && (transition.totalDurationNanos > 5000 || !transition.isRunning))
                Details(
                    Modifier,
                    availableTs,
                    selectedTs,
                    setSelectedTs
                ) { id ->
                        setCourtId(id)
                        setVisible(true)
                }
        }

        if (visible)
            AlertDialog(
                { setVisible(false) },
                {
                    Button(onClick = {
                        viewModel.reserve(date, courtId, selectedTs, context)
                        setSelectedTs(listOf())
                        setDate(LocalDate.MIN)
                        setAvailableTs(mapOf())
                        setVisible(false)
                        setDisplayGrid(false)
                    }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                },
                dismissButton = {
                    Button(onClick = { setVisible(false) }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
                text = {
                    Text(text = stringResource(R.string.do_you_want_to_confirm) +
                            "\n\n" + stringResource(id = R.string.date) + ": $date\n" +
                            stringResource(id = R.string.court) +
                            ": ${courts.value?.find { it.id == courtId }?.name}\n"
                    )
                },
                shape = RoundedCornerShape(10)
            )
    }
}

@Composable
private fun Calendar(
    modifier: Modifier,
    viewModel: CAViewModel,
    selectedDate: LocalDate,
    onDecorate: (LocalDate) -> Int,
    onSelect: (LocalDate) -> Unit
) {
    val currentMonth = YearMonth.now()
    val startMonth = currentMonth.minusMonths(12)
    val endMonth = currentMonth.plusMonths(12)

    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    HorizontalCalendar(
        modifier = modifier.padding(bottom = 0.dp),
        state = state,
        dayContent = { day ->
            if (
                day.date.isBefore(LocalDate.now().atStartOfDay().toLocalDate()) ||
                day.position != DayPosition.MonthDate
            )
                Day(day = day, color = MaterialTheme.colorScheme.background)
            else {
                val color = if (day.date == selectedDate) MaterialTheme.colorScheme.secondary
                else calendarColors(viewModel.timeSlots.value?.size?: 0, onDecorate(day.date))


                Day(
                    Modifier.clickable(
                        enabled = true,
                        onClick = {
                            onSelect(day.date)
                        }
                    ),
                    day,
                    color
                )
            }
        },
        calendarScrollPaged = true,
        monthHeader = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.yearMonth.month
                        .getDisplayName(TextStyle.FULL, java.util.Locale.getDefault())
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                DaysOfWeekTitle(daysOfWeek)
            }
        },
        contentPadding = PaddingValues(bottom = 0.dp)
    )
}

@Composable
private fun Details(
    modifier: Modifier,
    available: Map<TimeSlot, List<Info>>,
    selectedTs: List<String>,
    setSelectedTs: (List<String>) -> Unit,
    reserve: (String) -> Unit
) {
    Row(
        modifier
    ) {
        MyGrid(
            Modifier.weight(1f),
            stringResource(id = R.string.time_slot),
            available.map { map ->
                Info(
                    map.key,
                    map.value.any { it.available }
                )
            },
            selectedTs
        ) {
            val list = selectedTs.toMutableList()

            if (!list.remove(it))
                list.add(it)

            setSelectedTs(list)
        }

        if (selectedTs.isNotEmpty())
            MyGrid(
                Modifier.weight(1f),
                stringResource(id = R.string.court),
                available.filter {
                    it.key.id in selectedTs
                }.values.flatten().groupBy { Pair(it.id, it.text) }.map { map ->
                    Info(
                        map.key.first,
                        map.key.second,
                        map.value.none { !it.available }
                    )
                }
            ) {
                reserve(it)
            }
    }
}

@Composable
fun MyGrid(
    modifier: Modifier = Modifier,
    text: String,
    list: List<Info>,
    selected: List<String> = listOf(),
    onSelect: (String) -> Unit
) {
    Column(
        modifier.fillMaxHeight()
    ) {
        Text(
            text,
            Modifier
                .weight(0.2f)
                .fillMaxSize(),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        LazyVerticalGrid(
            GridCells.Adaptive(150.dp),
            Modifier
                .weight(0.9f)
                .fillMaxSize(),
            userScrollEnabled = true
        ) {
            items(list) {
                CustomButton(it.text, it.available, it.id in selected) {
                    onSelect(it.id)
                }
            }
        }
    }
}

@Composable
private fun CustomButton(text: String, enabled: Boolean, pressed: Boolean, onSelect: () -> Unit) {
    val color = if (pressed)
        MaterialTheme.colorScheme.tertiaryContainer
    else
        MaterialTheme.colorScheme.primaryContainer

    Button(
        { onSelect() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            color,
            MaterialTheme.colorScheme.contentColorFor(color),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(text)
    }
}