package com.example.courtreservationapp.ratingCourt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.courtreservationapp.LoadingAnimation2
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Court
import com.example.courtreservationapp.data.Rating
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.reservations.TextColumn
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RatingPage() {
    val viewModel = viewModel(RatingViewModel::class.java)

    val loading = viewModel.loading.observeAsState()
    val reservations = viewModel.reservations.observeAsState()
    val ratings = viewModel.ratings.observeAsState()

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (selected, setSelected) = remember { mutableStateOf<Reservation?>(null) }
    val (review, setReview) = remember { mutableStateOf<String?>(null) }
    val (rating, setRating) = remember { mutableStateOf(0f) }
    val (enabled, setEnabled) = remember { mutableStateOf(false) }

    val starConfig = RatingBarConfig().size(30.dp)

    val context = LocalContext.current

    if (loading.value == true)
        LoadingAnimation2()
    else {
        Column(Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                text = stringResource(R.string.rating_title),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

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
                            TextColumn(
                                text = stringResource(R.string.court),
                                title = true
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .padding(3.dp)
                        ) {
                            TextColumn(
                                text = stringResource(R.string.date),
                                title = true
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
                    reservations.value?.map {
                        ReservationList(
                            court = it.court!!,
                            date = it.date
                        ) {
                            setShowDialog(true)
                            setSelected(it)

                            val ra = ratings.value?.find { r -> r.reservationID == it.id }
                            setEnabled(ra == null)

                            if (ra != null) {
                                setRating(ra.pointRating.toFloat())
                                setReview(ra.descriptionRating)
                            }
                        }
                    }
                }

            }
        }

        if (showDialog)
            Dialog({
                setShowDialog(false)
                setReview(null)
                setRating(0f)
            }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RatingBar(
                        rating,
                        Modifier,
                        config = starConfig,
                        onValueChange = {
                            if (enabled)
                                setRating(it)
                        }
                    ) {
                        if (enabled)
                            setRating(it)
                    }

                    OutlinedTextField(
                        review ?: "",
                        { setReview(it) },
                        Modifier.padding(5.dp),
                        enabled = enabled,
                        placeholder = {
                            Text(
                                text = review ?: stringResource(id = R.string.insert_review),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        maxLines = 3,
                        minLines = 3,
                        colors = TextFieldDefaults.colors()
                    )

                    Row {
                        Column {
                            if (enabled)
                                Button(onClick = {
                                    if (selected != null)
                                        viewModel.rate(
                                            Rating(
                                                reservationID = selected.id!!,
                                                date = selected.date,
                                                courtID = selected.court!!.id,
                                                pointRating = rating.toInt(),
                                                descriptionRating = review ?: ""
                                            ),
											context
                                        )

                                    setShowDialog(false)
                                    setSelected(null)
                                    setReview(null)
                                    setRating(0f)
                                }) {
                                    Text(text = stringResource(id = R.string.ok))
                                }
                        }

                        Column {
                            Button(onClick = {
                                setShowDialog(false)
                                setSelected(null)
                                setReview(null)
                                setRating(0f)
                            }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun ReservationList(
    court: Court,
    date: String,
    onSelect: () -> Unit
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
                TextColumn(text = court.name)
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(3.dp)
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .padding(3.dp)
                    ) {
                        TextColumn(text = date)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(3.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (LocalDate.parse(
                        date,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    ).isBefore(LocalDate.now()))
                    Row {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = "editIcon",
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        onSelect()
                                    }
                                )
                        )
                    }
            }
        }
    }
}