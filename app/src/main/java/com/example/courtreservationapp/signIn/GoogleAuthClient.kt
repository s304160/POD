package com.example.courtreservationapp.signIn

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.courtreservationapp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthClient (
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val auth: FirebaseAuth
) {

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }
        catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }


    suspend fun signInWithIntent(intent: Intent): SignInState {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user

            SignInState(
                user = user,
                signInError = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInState(
                user = null,
                signInError = e.message
            )
        }
    }


    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }


    fun signOut(){
        try {
            oneTapClient.signOut()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

}
