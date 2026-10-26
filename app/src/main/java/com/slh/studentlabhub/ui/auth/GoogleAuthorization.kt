package com.slh.studentlabhub.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.data.remote.AuthState
import kotlinx.coroutines.launch
import com.slh.studentlabhub.R
import com.slh.studentlabhub.i18n.Strings

@Composable
fun rememberGoogleAuthorization(
    onAuthorized: (accessToken: String) -> Unit,
    onError: (message: String) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val authManager = remember(context) {
        (context.applicationContext as SlhApplication).container.googleAuthManager
    }
    val scope = rememberCoroutineScope()
    val latestOnAuthorized by rememberUpdatedState(onAuthorized)
    val latestOnError by rememberUpdatedState(onError)

    val consentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            latestOnError(Strings.get(R.string.sign_in_cancelled))
            return@rememberLauncherForActivityResult
        }
        try {
            when (val state = authManager.handleConsentResult(result.data)) {
                is AuthState.Authorized -> latestOnAuthorized(state.accessToken)
                is AuthState.NeedsUserConsent -> latestOnError(Strings.get(R.string.classroom_access_denied))
            }
        } catch (error: Exception) {
            latestOnError(error.toUserMessage())
        }
    }

    return remember(authManager, consentLauncher, scope) {
        {
            scope.launch {
                try {
                    when (val state = authManager.authorize()) {
                        is AuthState.Authorized -> latestOnAuthorized(state.accessToken)
                        is AuthState.NeedsUserConsent -> consentLauncher.launch(
                            IntentSenderRequest.Builder(state.pendingIntent.intentSender).build(),
                        )
                    }
                } catch (error: Exception) {
                    latestOnError(error.toUserMessage())
                }
            }
            Unit
        }
    }
}