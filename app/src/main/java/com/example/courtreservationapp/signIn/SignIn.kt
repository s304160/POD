package com.example.courtreservationapp.signIn

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.example.courtreservationapp.R
import kotlinx.coroutines.launch


@Composable
fun SignIn(
    authViewModel: AuthViewModel,
    mainActivity: ComponentActivity,
    googleAuthClient: GoogleAuthClient,
    navController: NavHostController
) {
    //check if the user is already logged in
    LaunchedEffect(key1 = authViewModel.signInState.value.user != null) {
        if(authViewModel.signInState.value.user != null)
            navController.navigate("YourReservations")

    }


    val oneTapLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if(result.resultCode == RESULT_OK) {
                mainActivity.lifecycleScope.launch {
                    val signInResult = googleAuthClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    authViewModel.createUserDocument(signInResult.user)
                    authViewModel.signInState.value = signInResult
                }
            }
        }
    )


    SignInForm(
        authViewModel = authViewModel,
        mainActivity = mainActivity
    )



    //Sign in with One Tap
    Box(modifier = Modifier
        .padding(20.dp)
        .fillMaxWidth()
        .fillMaxHeight(.8f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(onClick = {
            mainActivity.lifecycleScope.launch {
                val signInIntentSender = googleAuthClient.signIn()
                oneTapLauncher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }) {
            Text(text = stringResource(R.string.sign_in_with_onetap))
        }
    }
}




@Composable
fun SignInForm(
    authViewModel: AuthViewModel,
    mainActivity: ComponentActivity
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var createUser by remember { mutableStateOf(false) }

    val signInState = authViewModel.signInState

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment =  Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            enabled = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            enabled = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )


        if(!createUser) {
            Button(
                enabled = (password != "" && email != ""),
                onClick = { authViewModel.signInWithEmailPassword(mainActivity.applicationContext, email, password) }) {
                Text(text = "Sign In")
            }


            if(signInState.value.signInError != null) {
                Text(
                    text = signInState.value.signInError ?: "",
                    color = Color.Red
                )
            }

            TextButton(onClick = { createUser = true }) {
                Text(text = stringResource(R.string.create_a_new_account))
            }
        }
        else {
            Button(
                enabled = (password != "" && email != ""),
                onClick = {
                mainActivity.lifecycleScope.launch {
                    authViewModel.createUser(mainActivity.applicationContext, email, password)
                }
            }) {
                Text(text = stringResource(R.string.create_account))
            }
        }
    }
}





@Composable
fun SignOutButton(
    googleAuthClient: GoogleAuthClient,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    Button(onClick =  {
        lifecycleOwner.lifecycleScope.launch {
            authViewModel.resetState()
            googleAuthClient.signOut()
        }
        navController.navigate("SignIn")
    }) {
        Text(text = stringResource(R.string.sign_out))
    }
}




