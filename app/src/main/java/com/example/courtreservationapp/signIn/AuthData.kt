package com.example.courtreservationapp.signIn

import com.google.firebase.auth.FirebaseUser


data class SignInState(
    val user: FirebaseUser? = null,
    val signInError: String? = null
)

