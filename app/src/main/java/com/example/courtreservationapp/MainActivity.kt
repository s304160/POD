package com.example.courtreservationapp

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.courtreservationapp.courtavailability.CAViewModel
import com.example.courtreservationapp.courtavailability.CourtAvailability
import com.example.courtreservationapp.data.Reservation
import com.example.courtreservationapp.deleteBooking.DeleteBooking
import com.example.courtreservationapp.details.Details
import com.example.courtreservationapp.details.DetailsViewModel
import com.example.courtreservationapp.profile.ProfileViewModel
import com.example.courtreservationapp.profile.ShowProfile
import com.example.courtreservationapp.ratingCourt.RatingCourt
import com.example.courtreservationapp.reservations.ReservationsViewModel
import com.example.courtreservationapp.reservations.YourReservations
import com.example.courtreservationapp.ui.theme.CourtReservationAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

lateinit var mainViewModel: MainViewModel
lateinit var profileViewModel: ProfileViewModel
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = MainViewModel(applicationContext)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        profileViewModel.initialize(this,mainViewModel.getCurrentUser() ?: null, mainViewModel::getReservationsForUserFromDatabase)

        setContent {
            CourtReservationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DrawerCourt(mainActivity = this)
                }
            }
        }
    }
}

@Composable
fun DrawerCourt(mainActivity: ComponentActivity) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(text = "option 1")
                Text(text = "option 2")
                Text(text = "option 3")
            }
        },
    ) {
        ScaffoldComposable(mainActivity, drawerState)
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun ScaffoldComposable(mainActivity: ComponentActivity, drawerState: DrawerState) {

    val navController = rememberNavController()
    var showFloatingButton: Boolean = rememberSaveable { mutableStateOf(true) }.value
    var showLoader = mainViewModel.showLoader.observeAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    showFloatingButton = when (navBackStackEntry?.destination?.route) {
        "RateCourt" -> false // on this screen bottom bar should be hidden
        "CourtAvailability" -> false
        "DeleteBooking" -> false // here too
        else -> true // in all other cases show bottom bar
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(modifier = Modifier, navController = navController) },

        content = {
            if(showLoader.value==true)
                LoadingAnimation2()
            else
            Navigation(
                modifier = Modifier.padding(it),
                mainActivity = mainActivity,
                navController = navController,
            )
        },

        )
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier, navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }
    NavigationBar(Modifier.background(Color.Blue)) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == 0,
            onClick = { selectedItem = 0; navController.navigate("YourReservations") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Book Courts") },
            label = { Text("Book Courts") },
            selected = selectedItem == 1,
            onClick = { selectedItem = 1; navController.navigate("CourtAvailability") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Rate Courts") },
            label = { Text("Rate Courts") },
            selected = selectedItem == 2,
            onClick = { navController.navigate("RateCourt") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Book Courts") },
            label = { Text("Profile") },
            selected = selectedItem == 1,
            onClick = { navController.navigate("Profile") }
        )
    }
}


@Composable
fun Navigation(
    modifier: Modifier,
    mainActivity: ComponentActivity,
    navController: NavHostController
) {

    val reservationViewModel = ReservationsViewModel()
    val availabilityVM = CAViewModel(mainActivity.applicationContext)
    val detailsViewModel = ViewModelProvider(mainActivity)[DetailsViewModel::class.java]

    profileViewModel.setListSport(mainViewModel.sports)
    reservationViewModel.prepareViewModel(mainViewModel.getCurrentUser(),mainViewModel.reservationsCurrentUser.value!!, mainViewModel.courts.value!!,mainViewModel.sports.value!!, mainViewModel.timeslots.value!!)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "YourReservations"
    ) {
        composable("YourReservations") {
            YourReservations(
                reservationsViewModel = reservationViewModel,
                detailsViewModel = detailsViewModel,
                navController = navController
            )
        }

        composable("CourtAvailability") {
            CourtAvailability(viewModel = availabilityVM)
        }

        composable("Details") {
            if (navController.previousBackStackEntry?.savedStateHandle?.contains("reservation")!!) {
                var reservationToChange: Reservation = navController.previousBackStackEntry?.savedStateHandle?.get<Reservation>("reservation")!!
                detailsViewModel.prepareViewModel(reservationSelected = reservationToChange, unavailableTimeSlot = mainViewModel.unavailableSlotForCourtIDAndDate(reservationToChange.court?.id,reservationToChange.date), timeSlot = mainViewModel.timeslots.value!!,userID = mainViewModel.getCurrentUser()?.getIDUser()!!)
                Details(detailsViewModel, navController , mainViewModel::updatingReservation)
            }

        }

        composable("RateCourt") {
            RatingCourt(ratingMethod = mainViewModel::ratingCourt, navController = navController,listCourt = mainViewModel.courts.value!!, userID = mainViewModel.getCurrentUser()?.getIDUser() ?: "")
        }

        composable("Profile") {
            ShowProfile(LocalContext.current, profileViewModel)
        }


        composable("DeleteBooking") { backStackEntry ->
            if (navController.previousBackStackEntry?.savedStateHandle?.contains("reservation")!!) {
                var bookingToDelete: Reservation = navController.previousBackStackEntry?.savedStateHandle?.get<Reservation>("reservation")!!
                DeleteBooking(
                    reservation = bookingToDelete,
                    navController = navController,
                    deleteBookingFunction = mainViewModel::deleteBooking
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarCourt(drawerState: DrawerState) {

    val scope = rememberCoroutineScope()

    TopAppBar(
        title = { Text(text = "Top Bar") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.apply {
                        if (isClosed)
                            drawerState.open()
                        else close()
                    }
                }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "menu")
            }
        }
    )
}

@Composable
    fun LoadingAnimation2(
    circleColor: Color = colorResource(id = R.color.blue_800),
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
            Box() {
                // animating circles
                circles.forEachIndexed { index, animatable ->
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
            Text(text = "Loading...", modifier = Modifier ,fontSize = 25.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.blue_800))
        }
    }
