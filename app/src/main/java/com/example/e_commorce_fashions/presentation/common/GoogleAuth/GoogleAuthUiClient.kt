package com.example.e_commorce_fashions.presentation.common.GoogleAuth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.e_commorce_fashions.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(private val context: Context, private val onTapClient: SignInClient) {

    suspend fun getIntentSender(): IntentSender? {
        return try{
            val signInRequest = buildGoogleSignInRequest()
            val result = onTapClient.beginSignIn(signInRequest).await()
            result.pendingIntent.intentSender
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    fun signInWithIntent(intent: Intent) : SignInCredential {
        return onTapClient.getSignInCredentialFromIntent(intent)
    }

    private fun buildGoogleSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}