package com.example.courtreservationapp.ratingCourt

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Court
import com.example.courtreservationapp.data.Rating
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import es.dmoral.toasty.Toasty
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RatingCourt(
    ratingMethod: (Rating) -> Unit,
    navController: NavController,
    listCourt: List<Court> = listOf(),
    userID: String = ""
) {

    val context: Context = LocalContext.current
    var courtSelected: MutableState<Court?> = remember { mutableStateOf(null) }
    var rating = remember { mutableStateOf(-1f) }
    val otherCommentText = remember { mutableStateOf("") }
    val emoji = remember {
        derivedStateOf {
            when (rating.value) {
                0f -> "\uD83D\uDE2D️"
                1f -> "\uD83D\uDE2D️"
                2f -> "\uD83D\uDE22️"
                3f -> "\uD83D\uDE1F️"
                4f -> "\uD83D\uDE42️"
                else -> "\uD83D\uDE01️"
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.rating_description_step1),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            SearchCourtsComponent(courtSelected = courtSelected, list = listCourt)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.rating_description_step2),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            RatingBar(
                value = rating.value,
                onValueChange = { rating.value = it },
                onRatingChanged = {},
                config = RatingBarConfig().size(50.dp))
            AnimatedContent(targetState = emoji) {
                Text(text = it.value, fontSize = 80.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.rating_description_step3),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            OtherCommentTextBox(otherCommentText = otherCommentText)
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Unspecified,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(130.dp),
                    onClick = { navController.navigate(navController.previousBackStackEntry?.destination?.route.orEmpty()) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Back", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Button(
                    enabled = courtSelected.value != null && rating.value != -1f,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.green_700),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(130.dp)
                        .border(2.dp, Color.White, shape),
                    onClick = {
                        var tmpRating = Rating(
                            userID = userID,
                            courtID = courtSelected.value?.id!!,
                            pointRating = rating.value.toInt(),
                            descriptionRating = otherCommentText.value,
                            dateRating = LocalDateTime.now().toString()
                        )
                        if (tmpRating.isValid()) {
                            ratingMethod(tmpRating)
                        } else {
                            Log.d(
                                "Rating Firestore",
                                "Error Saving the Rating ... error : rating not valid ..."
                            )
                            Toasty.error(
                                context,
                                "Error saving the reservation !!!",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                        navController.navigate(navController.previousBackStackEntry?.destination?.route.orEmpty())
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_save_default_black),
                        contentDescription = "Save Rate Court"
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Save", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun SearchCourtsComponent(
    modifier: Modifier = Modifier,
    courtSelected: MutableState<Court?>,
    list: List<Court>
) {

    var dropDownExpanded = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth(0.7f)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(2.dp, Color.Black, shape)
                .padding(top = 5.dp, bottom = 5.dp, start = 15.dp)
                .clickable { dropDownExpanded.value = !dropDownExpanded.value }) {
            Text(
                text = if (courtSelected.value != null) courtSelected.value!!.name else "Select Court ...",
                fontSize = 15.sp,
                color = Color.Black
            )
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "image")
        }
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .align(Alignment.CenterHorizontally),
            expanded = dropDownExpanded.value,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = { dropDownExpanded.value = false }) {

            list.forEach { court: Court ->
                val iconSportItem: Painter?
                when (court.sportID) {
                    1 -> iconSportItem =
                        painterResource(id = R.drawable.icon_football_default_black)

                    2 -> iconSportItem = painterResource(id = R.drawable.icon_basket_default_black)
                    3 -> iconSportItem = painterResource(id = R.drawable.icon_tennis_default_black)
                    else -> iconSportItem =
                        painterResource(id = R.drawable.icon_football_default_black)
                }

                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = iconSportItem,
                            contentDescription = "Icon Sport"
                        )
                    },
                    text = { Text(text = court.name) },
                    onClick = { courtSelected.value = court; dropDownExpanded.value = false })
            }
        }
    }
}

@Composable
fun OtherCommentTextBox(
    modifier: Modifier = Modifier,
    otherCommentText: MutableState<String> = mutableStateOf("")
) {

    BasicTextField(
        value = otherCommentText.value,
        onValueChange = { otherCommentText.value = it },
        maxLines = 3,
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(100.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(2.dp, Color.Black)
            .background(colorResource(id = R.color.inactive_text_color))
            .padding(18.dp),
    )
}