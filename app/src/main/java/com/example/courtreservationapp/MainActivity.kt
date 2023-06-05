package com.example.courtreservationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.courtreservationapp.courtavailability.ReservationPage
import com.example.courtreservationapp.deleteBooking.DeleteBooking
import com.example.courtreservationapp.details.Details
import com.example.courtreservationapp.profile.Profile
import com.example.courtreservationapp.ratingCourt.RatingPage
import com.example.courtreservationapp.reservations.YourReservations
import com.example.courtreservationapp.signIn.GoogleAuthClient
import com.example.courtreservationapp.signIn.SignIn
import com.example.courtreservationapp.signIn.AuthViewModel
import com.example.courtreservationapp.ui.theme.CourtReservationAppTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CourtReservationAppTheme {
                val authViewModel = viewModel(AuthViewModel::class.java)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScaffoldComposable(
                        mainActivity = this,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ScaffoldComposable(
    mainActivity: ComponentActivity,
    authViewModel: AuthViewModel,
) {
    val googleAuthClient by lazy {
        GoogleAuthClient(
            context = mainActivity.applicationContext,
            oneTapClient = Identity.getSignInClient(mainActivity.applicationContext),
            auth = authViewModel.auth
        )
    }

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if(authViewModel.signInState.value.user != null) {
                BottomNavigationBar(modifier = Modifier, navController = navController)
            }
        },

        content = {
            Navigation(
                modifier = Modifier.padding(it),
                mainActivity = mainActivity,
                navController = navController,
                googleAuthClient = googleAuthClient,
                authViewModel = authViewModel
            )
        }
    )


}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier, navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }
    NavigationBar(modifier.background(Color.Blue)) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text(stringResource(R.string.home)) },
            selected = selectedItem == 0,
            onClick = { selectedItem = 0; navController.navigate("YourReservations") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Book Courts") },
            label = { Text(stringResource(R.string.book_courts)) },
            selected = selectedItem == 1,
            onClick = { selectedItem = 1; navController.navigate("CourtAvailability") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Rate Courts") },
            label = { Text(stringResource(R.string.rate_courts)) },
            selected = selectedItem == 2,
            onClick = { selectedItem = 2; navController.navigate("RateCourt") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Book Courts") },
            label = { Text(stringResource(R.string.profile)) },
            selected = selectedItem == 3,
            onClick = { selectedItem = 3; navController.navigate("Profile") }
        )
    }
}


@Composable
fun Navigation(
    modifier: Modifier,
    mainActivity: ComponentActivity,
    navController: NavHostController,
    googleAuthClient: GoogleAuthClient,
    authViewModel: AuthViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "SignIn"
    ) {
        composable("YourReservations") {
            YourReservations(
                navController = navController
            )
        }

        composable("CourtAvailability") {
            ReservationPage()
        }

        composable("Details") {
            if (navController.previousBackStackEntry?.savedStateHandle?.contains("reservation")!!) {
                val reservationID = navController.previousBackStackEntry?.savedStateHandle?.get<String>("reservation")!!
                Details(navController, reservationID)
            }

        }
        composable("RateCourt") {
            RatingPage()
        }

        composable("Profile") {            
            Profile(authViewModel, navController)
        }

        composable("DeleteBooking") {
            if (navController.previousBackStackEntry?.savedStateHandle?.contains("reservation")!!) {
                val reservationToDelete = navController.previousBackStackEntry?.savedStateHandle?.get<String>("reservation")!!
                DeleteBooking(
                    reservationID = reservationToDelete,
                    navController = navController,
                )
            }
        }
        composable("SignIn") {
            SignIn(
                authViewModel = authViewModel,
                mainActivity = mainActivity,
                googleAuthClient = googleAuthClient,
                navController = navController
            )
        }

    }
}

@Composable
fun LoadingAnimation2(
    circleColor: Color = MaterialTheme.colorScheme.primary,
    animationDelay: Int = 1000
) {
    // 3 circles
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            // Use coroutine delay to sync animations
            // divide the animation delay by number of circles
            delay(timeMillis = (animationDelay / 3L) * (index + 1))

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // outer circle
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            // animating circles
            circles.forEachIndexed { _, animatable ->
                Box(
                    modifier = Modifier
                        .scale(scale = animatable.value)
                        .size(size = 300.dp)
                        .clip(shape = CircleShape)
                        .background(
                            color = circleColor.copy(alpha = (1 - animatable.value))
                        )
                )
            }
        }
        Text(text = stringResource(R.string.loading), modifier = Modifier ,fontSize = 25.sp, fontWeight = FontWeight.Bold, color = circleColor)
    }
}
