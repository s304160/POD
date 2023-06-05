package com.example.courtreservationapp.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.courtreservationapp.R
import com.example.courtreservationapp.courtavailability.MyGrid
import com.example.courtreservationapp.data.Info

@Composable
fun Details(
    navController: NavController,
    reservationID: String
) {
    val viewModel = viewModel(DetailsViewModel::class.java)
    viewModel.setup(reservationID)

    val description = viewModel.description.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ButtonList(Modifier.weight(2f), viewModel)

        Text(text = stringResource(id = R.string.additional_requests), style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = description.value?: "",
            onValueChange = { viewModel.setDescription(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = viewModel.reservationToChange.value?.description ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )

        Spacer(Modifier.weight(1f))

        ConfirmationSection(viewModel, navController)
    }
}

@Composable
fun ButtonList(modifier: Modifier, viewModel: DetailsViewModel) {
    val timeslots = viewModel.timeSlots.observeAsState()
    val unavailableTimeslots = viewModel.unavailableTimeSlots.observeAsState()
    val selectedTimeslots = viewModel.selectedTimeSlots.observeAsState()

    MyGrid(
        modifier = modifier,
        text = stringResource(id = R.string.time_slot),
        list = timeslots.value?.map {
            Info(it, unavailableTimeslots.value?.contains(it.id) != true )
        }?: listOf(),
        selected = selectedTimeslots.value?.map { it.id } ?: listOf()
    ) {
        viewModel.selectTimeSlot(it)
    }
}

@Composable
fun ConfirmationSection(
    viewModel: DetailsViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val selectedTimeSlots = viewModel.selectedTimeSlots.observeAsState()

    Row {
        Button(
            onClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "reservation",
                    viewModel.reservationToChange.value!!.id
                )
                navController.navigate("DeleteBooking")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            ),
            shape = MaterialTheme.shapes.small // Use small shape for sharper angles
        ) {
            Text(text = stringResource(id = R.string.delete), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.updateReservation(context)
                navController.navigate(navController.previousBackStackEntry?.destination?.route.orEmpty())
            },
            enabled = selectedTimeSlots.value?.isNotEmpty() == true,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.small // Use small shape for sharper angles
        ) {
            Text(text = stringResource(id = R.string.modify), style = MaterialTheme.typography.titleMedium)
        }
    }
}
