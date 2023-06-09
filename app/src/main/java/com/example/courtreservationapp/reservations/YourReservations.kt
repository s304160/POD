package com.example.courtreservationapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.courtreservationapp.MainViewModel
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Court
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.database.entities.CourtEntity
import com.example.courtreservationapp.details.DetailsViewModel
import com.example.courtreservationapp.mainViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.format.TextStyle as javaTextStyle


@Composable
fun YourReservations(
    reservationsViewModel: ReservationsViewModel,
    detailsViewModel: DetailsViewModel,
    navController: NavHostController
) {

    val reservations = mainViewModel.reservationsCurrentUser.observeAsState().value
    var (showedReservations, setShowedReservations) = remember { mutableStateOf<List<Reservation>>(listOf())}

    val (showReservations, setShowReservations) = remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.yourReservations),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )


        val selectDay: (day: CalendarDay) -> Unit = { day ->
            run {
                val tmpReservationForDate  = reservations?.filter { it.date == day.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) }
                if (tmpReservationForDate!!.isNotEmpty()) {
                    setShowReservations(true)
                    setShowedReservations(tmpReservationForDate)
                } else
                    setShowReservations(false)
            }
        }



        Calendar(reservations, selectDay)


        if (showReservations)
            ReservationsDetail(showedReservations, navController, detailsViewModel)


    }
}


@Composable
fun Calendar(reservations: List<Reservation>?, selectDay: (CalendarDay) -> Unit) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }      // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(12) }            // Adjust as needed
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )



    HorizontalCalendar(
        state = state,
        dayContent = { day ->
            Day(modifier = Modifier
                .clickable(
                    enabled = true,
                    onClick = { selectDay(day) }
                ), day, decorate(day, reservations))
        },
        calendarScrollPaged = true,
        monthHeader = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.yearMonth.month.name,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                DaysOfWeekTitle(daysOfWeek = daysOfWeek)
            }
        },
    )
}


fun decorate(day: CalendarDay, reservations: List<Reservation>?): Color {
    val date = day.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

    var color = Color.White

    if (reservations != null)
        if (reservations?.map { it.date }?.contains(date) == true)
            color = Color.Green

    return color
}


@Composable
fun Day(modifier: Modifier = Modifier, day: CalendarDay, color: Color) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.position == DayPosition.MonthDate) Color.Black else Color.Gray,
        )

    }
}


@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(javaTextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}


@Composable
fun ReservationsDetail(
    reservationsByDate: List<Reservation>,
    navController: NavHostController,
    detailsViewModel: DetailsViewModel
) {

    //header
    Row {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(3.dp)
                ) {
                    TextColumn(text = stringResource(R.string.court), fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(3.dp)
                ) {
                    TextColumn(
                        text = stringResource(R.string.time_slot),
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(3.dp)
                ) {}
            }
        }
    }


    Row(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {

        Column {
            for (reservation in reservationsByDate) {
                ReservationColumns(
                    reservation = reservation,
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun ReservationColumns(
    reservation: Reservation?,
    navController: NavHostController
) {

    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(3.dp)
            ) {
                TextColumn(text = reservation?.court?.name.toString())
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(3.dp)
            ) {
                reservation?.listTimeSlot?.sortedBy { it.id }?.forEach { timeslot ->
                    Row {
                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .padding(3.dp)
                        ) {
                            TextColumn(text = timeslot.timeslot)
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = "editIcon",
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("reservation", reservation)
                                    navController.navigate("Details")
                                }
                            ),
                    )
                    Icon(
                        Icons.Rounded.Delete, contentDescription = "deleteIcon",
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = true,
                                onClick = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("reservation", reservation)
                                    navController.navigate("DeleteBooking")
                                })
                    )
                }
            }
        }
    }

}


@Composable
fun TextColumn(text: String, fontWeight: FontWeight = FontWeight.Normal) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        fontSize = 20.sp,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        fontWeight = fontWeight
    )
}