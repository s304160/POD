package com.example.courtreservationapp.courtavailability

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.courtreservationapp.data.Court
import com.example.courtreservationapp.data.Info
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.TimeSlot
import com.example.courtreservationapp.reservations.Day
import com.example.courtreservationapp.reservations.DaysOfWeekTitle
import com.example.courtreservationapp.ui.theme.Teal700
import com.example.courtreservationapp.ui.theme.calendarColors
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

fun addDate(
    date: String,
    courts: List<Court>?,
    reservations: List<Reservation>?,
    timeSlots: List<TimeSlot>?
): Map<TimeSlot, List<Info>> {
    val bookedToday = reservations?.filter { it.date == date }

    return timeSlots?.groupBy { it }?.mapValues { timeSlot ->
        courts?.map { court ->
            var availableSlot:Boolean = if(bookedToday?.none { it.listTimeSlot?.firstOrNull{it?.id == timeSlot.key.id}!=null && it.court?.name == court.name && it.date == date } ==null) false else true
            Info(court,availableSlot)
        } ?: listOf()
    } ?: mapOf()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CourtAvailability(viewModel: CAViewModel) {
    val (expanded, setExpanded) = remember {
        mutableStateOf(false)
    }
    val (visibleDialogConfirm, setVisibleDialogConfirm) = remember {
        mutableStateOf(false)
    }

    val (sportId, setSportId) = remember {
        mutableStateOf(1)
    }
    val (date, setDate) = remember {
        mutableStateOf(LocalDate.MIN)
    }
    val (courtId, setCourtId) = remember {
        mutableStateOf(0)
    }
    val (displayGrid, setDisplayGrid) = remember {
        mutableStateOf(false)
    }

    val (availableTs, setAvailableTs) = remember {
        mutableStateOf(mapOf<TimeSlot, List<Info>>())
    }
    val (selectedTs, setSelectedTs) = remember {
        mutableStateOf(listOf<Int>())
    }

    val courts = viewModel.courts.observeAsState()
    val reservations = viewModel.reservationsCurrentUser.observeAsState()
    val sports = viewModel.sports.observeAsState()
    val timeSlots = viewModel.timeslots.observeAsState()

    val available = remember {
        mutableMapOf<String, Map<TimeSlot, List<Info>>>()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Button(
            { setExpanded(!expanded) },
            Modifier
                .height(60.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                Color.Gray,
                Color.Black
            )
        ) {
            Text(
                (sports.value?.find { it.id == sportId }?.name ?: "")
                    .replaceFirstChar { it.uppercase() },
                Modifier.fillMaxSize(),
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )
        }
        DropdownMenu(expanded, { setExpanded(false) }, Modifier.fillMaxSize()) {
            sports.value?.filter {
                it.id != sportId
            }?.map {
                DropdownMenuItem(
                    {
                        Text(
                            it.name.replaceFirstChar { it.uppercase() },
                            Modifier.fillMaxSize(),
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    {
                        setSportId(it.id)
                        setExpanded(false)
                        setDate(LocalDate.MIN)
                    }
                )
            }
        }

        Calendar(
            Modifier,
            date,
            { day ->
                val dateString = day.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                val availableTimeSlots = addDate(
                    dateString,
                    courts.value?.filter { it.sportID.toInt() == sportId },
                    reservations.value,
                    timeSlots.value
                )

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
                  modifier =   Modifier,
                  available =   availableTs,
                  selectedTs =   selectedTs,
                  setSelectedTs =   setSelectedTs
                ) { courtID ->
                    setCourtId(courtID)
                    setVisibleDialogConfirm(true)
                }
        }

        if (visibleDialogConfirm)
            AlertDialog(
                onDismissRequest =  { setVisibleDialogConfirm(false) },
                confirmButton =  {
                    Button(onClick = {
                        viewModel.reserve(date, courtId, selectedTs)
                        setSelectedTs(listOf())
                        setDate(LocalDate.MIN)
                        setAvailableTs(mapOf())
                        setVisibleDialogConfirm(false)
                        setDisplayGrid(false)
                    }) {
                        Text(text = "Ok")
                    }
                },
                dismissButton = {
                    Button(onClick = { setVisibleDialogConfirm(false) }) {
                        Text(text = "Cancel")
                    }
                },
                text = {
                    Text(text = "Do you want to confirm?\n\n" +
                            "Date: $date\n" +
                            "Court: ${courts.value?.find { it.id == courtId }?.name}\n"+
                            "Timeslot : ${timeSlots.value?.filter {ts -> selectedTs.find { it == ts.id}!=null }?.map { it.timeslot }}"

                    )
                },
                shape = RoundedCornerShape(10)
            )
    }
}

@Composable
private fun Calendar(
    modifier: Modifier,
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
                Day(day = day, color = Color.White)
            else {
                val color = if (day.date == selectedDate) Teal700
                else calendarColors[onDecorate(day.date)] ?: Color.White

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
                    text = it.yearMonth.month.name,
                    style = MaterialTheme.typography.headlineMedium,
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
    selectedTs: List<Int>,
    setSelectedTs: (List<Int>) -> Unit,
    reserve: (Int) -> Unit
) {
    Row(
        modifier
    ) {
        MyGrid(
           modifier =  Modifier.weight(1f),
           text =  "Time Slots",
           list = available.map { map ->
                Info(
                    map.key,
                    map.value.any { it.available }
                )
            },
            selected = selectedTs
        ) {
            val list = selectedTs.toMutableList()

            if (!list.remove(it))
                list.add(it)

            setSelectedTs(list)
        }

        if (selectedTs.isNotEmpty())
            MyGrid(
                modifier = Modifier.weight(1f),
                text = "Courts",
                list = available.filter { available_unit ->
                    selectedTs.find {  it === available_unit.key.id} !=null
                }.values.flatten().groupBy { Pair(it.id, it.text) }.map { map ->
                    Info(
                        map.key.first,
                        map.key.second,
                        map.value.none { !it.available }
                    )
                }
            ) {
                if(it!=0)
                    reserve(it)
            }
    }
}

@Composable
private fun MyGrid(
    modifier: Modifier,
    text: String,
    list: List<Info>,
    selected: List<Int> = listOf(),
    onSelect: (Int) -> Unit
) {
    Column(
        modifier.fillMaxHeight()
    ) {
        Text(
            text,
            Modifier
                .weight(0.2f)
                .fillMaxSize(),
            fontSize = 25.sp,
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
        Color.Red
    else
        Color.Blue

    Button(
        { onSelect() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            color,
            Color.White,
            Color.White,
            Color.Black
        )
    ) {
        Text(text)
    }
}