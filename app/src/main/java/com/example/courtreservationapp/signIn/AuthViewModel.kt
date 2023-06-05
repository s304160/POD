package com.example.courtreservationapp.signIn

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthViewModel: ViewModel() {
    val auth = Firebase.auth
    private val db = Firebase.firestore

    private var user: User? = null

    var signInState = mutableStateOf(SignInState())

    fun createUser(context: Context, email: String, password: String) {
        //check if user exists
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                //if user doesn't exist create it
                if(it.documents.isEmpty()) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                createUserDocument(task.result.user)
                                signInState.value = SignInState(
                                    user = auth.currentUser,
                                    signInError = null
                                )
                            } else {
                                signInState.value = SignInState(
                                    user = null,
                                    signInError = context.getString(R.string.error_creation_of_new_user_failed)
                                )
                            }
                        }
                }
                else {
                    signInState.value = SignInState(
                        user = null,
                        signInError = context.getString(R.string.error_user_already_exists)
                    )
                }

            }
            .addOnFailureListener {
                signInState.value = SignInState(
                    user = null,
                    signInError = it.message ?: context.getString(R.string.error_creation_of_new_user_failed)
                )
            }
    }

    fun createUserDocument(currentUser: FirebaseUser?) {
        db.collection("Users").document(currentUser!!.uid)
            .set(User())
            .addOnCompleteListener{
                signInState.value = SignInState(currentUser, null)
            }
            .addOnFailureListener { exception ->
                signInState.value = SignInState(null, exception.message)
            }
    }

    fun signInWithEmailPassword(context: Context, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signInState.value = SignInState(
                        user = auth.currentUser,
                        signInError = null
                    )
                } else {
                    signInState.value = SignInState(
                        user = null,
                        signInError = context.getString(R.string.username_or_password_not_correct)
                    )
                }
            }
    }


    fun resetState() {
        signInState.value = SignInState()
        user = null
    }
}