package com.example.courtreservationapp.details

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.data.TimeSlot
import es.dmoral.toasty.Toasty

@Composable
fun Details(
    viewModel: DetailsViewModel,
    navController: NavController,
    updateReservation: (Reservation,MutableList<TimeSlot>?,String?) -> Unit
) {

    val description = viewModel.description.observeAsState()
    val unavailableTimeslots = viewModel.unavailableTimeSlots.observeAsState()
    val selectedTimeslots = viewModel.selectedTimeSlots.observeAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Timeslots")

        ButtonList(viewModel)

        Text(text = "Description")

        OutlinedTextField(
            value = description.value!!,
            onValueChange = { viewModel.description.value = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        ConfirmationSection(viewModel, navController, updateReservation)
    }
}

@Composable
fun ButtonList(viewModel: DetailsViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {

        val timeslots = viewModel.timeSlots.observeAsState()

        val rows = timeslots.value!!.chunked(3)
        val lastRowIndex = rows.lastIndex

        rows.forEachIndexed { rowIndex, rowButtons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowButtons.forEachIndexed { index, buttonText ->
                    val color = viewModel.defineButtonColor(buttonText.id)

                    Button(
                        onClick = { viewModel.selectTimeSlot(buttonText.id) },
                        modifier = Modifier.weight(1f),
                        enabled = color != Color.Gray,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = color, // Set the text color of the button
                        )
                    ) {
                        Text(text = buttonText.timeslot)
                    }
                }
                if (rowIndex == lastRowIndex) {
                    val remainingButtons = 3 - rowButtons.size
                    repeat(remainingButtons) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ConfirmationSection(
    viewModel: DetailsViewModel,
    navController: NavController,
    updateReservation: (Reservation,MutableList<TimeSlot>?,String?) -> Unit
) {
    val context: Context = LocalContext.current

    Row {
        Button(
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "reservation",
                    viewModel.reservationSelected.value
                )
                navController.navigate("DeleteBooking")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.small // Use small shape for sharper angles
        ) {
            Text(text = "DELETE")
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                updateReservation(viewModel.reservationSelected.value!!,viewModel.selectedTimeSlots.value,viewModel.description.value)
                navController.navigate(navController.previousBackStackEntry?.destination?.route.orEmpty())
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.small // Use small shape for sharper angles
        ) {
            Text(text = "MODIFY")
        }
    }
}
