package com.example.courtreservationapp.deleteBooking

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Reservation
import es.dmoral.toasty.Toasty

@Composable
fun DeleteBooking(
    reservation: Reservation,
    navController: NavController,
    deleteBookingFunction: (String) -> Unit
) {
    val context: Context = LocalContext.current
    Surface(
        modifier = Modifier
            .padding(10.dp)
            .border(2.dp, Color.Black, shape)
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
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
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
                    Text(text = stringResource(R.string.cancellationBooking_description_court))
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(
                        text = reservation.court?.name.orEmpty(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
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
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Date Booking")
                    Text(text = stringResource(R.string.cancellationBooking_description_date))
                }
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(1f)
                        .heightIn(25.dp, 80.dp)
                ) {
                    items(reservation.listTimeSlot?.size!!) { timeslot ->
                        Text(
                            text = reservation.date!! + " - " + reservation.listTimeSlot.get(
                                timeslot
                            )?.timeslot,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }
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
                    if (reservation.sport?.name.equals("soccer"))
                        Icon(
                            painter = painterResource(id = R.drawable.icon_football_default_black),
                            contentDescription = "Sport Booking"
                        )
                    if (reservation.sport?.name.equals("basket"))
                        Icon(
                            painter = painterResource(id = R.drawable.icon_basket_default_black),
                            contentDescription = "Sport Booking"
                        )
                    if (reservation.sport?.name.equals("tennis"))
                        Icon(
                            painter = painterResource(id = R.drawable.icon_tennis_default_black),
                            contentDescription = "Sport Booking"
                        )

                    Text(text = stringResource(R.string.cancellationBooking_description_sport))
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(
                        text = reservation.sport?.name.orEmpty(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }

            Row(modifier = Modifier.padding(top = 30.dp)) {
                Text(
                    text = stringResource(R.string.cancellationBookingDescription_part2),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f, true))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(130.dp)
                        .border(1.dp, Color.Black, shape),
                    onClick = { navController.navigate(navController.previousBackStackEntry?.destination?.route.orEmpty()) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Undo Delete Booking",
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Back", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(130.dp)
                        .border(2.dp, Color.White, shape),
                    onClick = {
                        deleteBookingFunction(reservation.id)
                        Toasty.success(context, "Booking Deleted !!!", Toast.LENGTH_SHORT, true)
                            .show()
                        navController.navigate("YourReservations")
                    }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Booking")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Delete", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewDeleteBooking() {
    //DeleteBooking()
}