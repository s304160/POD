package com.example.courtreservationapp.deleteBooking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.courtreservationapp.R

@Composable
fun DeleteBooking(
    reservationID: String,
    navController: NavController
) {
    val viewModel = viewModel(DeleteViewModel::class.java)
    val reservation = viewModel.getReservation(reservationID).observeAsState()

    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = stringResource(R.string.cancellationBookingDescription_part1),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Court Booking")
                    Text(
                        text = stringResource(R.string.court) + ":",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(
                        text = reservation.value?.court?.name.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(30.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Date Booking")
                    Text(
                        text = stringResource(R.string.date) + ":",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(
                        text = reservation.value?.date?: "",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(0.8f)
                    .weight(2f)
                    .padding(top = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Date Booking")
                    Text(
                        text = stringResource(R.string.time_slot) + ":",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(reservation.value?.listTimeSlot?.size?: 0) {
                        Text(
                            text = reservation.value?.listTimeSlot?.get(it)?.timeslot!!,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier,
                    onClick = { navController.navigate(navController.previousBackStackEntry?.destination?.route.orEmpty()) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Undo Delete Booking",
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.cancellationBooking_button_back), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier,
                    onClick = {
                        viewModel.deleteBooking(reservation.value?.id?: "", context)
                        navController.navigate("YourReservations")
                    }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Booking")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.delete), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}