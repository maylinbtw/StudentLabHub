package com.slh.studentlabhub.data.remote

import android.accounts.Account
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.ClearTokenRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed interface AuthState {
    data class Authorized(val accessToken: String) : AuthState
    data class NeedsUserConsent(val pendingIntent: PendingIntent) : AuthState
}

class GoogleAuthManager(context: Context) {

    private val client = Identity.getAuthorizationClient(context.applicationContext)

    private val scopes: List<Scope> = SCOPES.map { Scope(it) }

    private val request: AuthorizationRequest = AuthorizationRequest.builder()
        .setRequestedScopes(scopes)
        .build()

    suspend fun authorize(): AuthState {
        val result = client.authorize(request).awaitTask()
        return result.toAuthState()
    }

    fun handleConsentResult(data: Intent?): AuthState {
        val result = client.getAuthorizationResultFromIntent(data)
        return result.toAuthState()
    }

    suspend fun clearToken(accessToken: String) {
        runCatching {
            client.clearToken(
                ClearTokenRequest.builder()
                    .setToken(accessToken)
                    .build(),
            ).awaitTask()
        }
    }

    suspend fun revokeAccess(email: String) {
        runCatching {
            client.revokeAccess(
                RevokeAccessRequest.builder()
                    .setAccount(Account(email, ACCOUNT_TYPE_GOOGLE))
                    .setScopes(scopes)
                    .build(),
            ).awaitTask()
        }
    }

    private fun AuthorizationResult.toAuthState(): AuthState {
        val intent = pendingIntent
        if (hasResolution() && intent != null) {
            return AuthState.NeedsUserConsent(intent)
        }
        val token = accessToken ?: throw IllegalStateException("Google не вернул токен доступа")
        return AuthState.Authorized(token)
    }

    companion object {
        private const val ACCOUNT_TYPE_GOOGLE = "com.google"

        val SCOPES = listOf(
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/classroom.courses.readonly",
            "https://www.googleapis.com/auth/classroom.coursework.me.readonly",
            "https://www.googleapis.com/auth/classroom.rosters.readonly",
        )
    }
}

suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { error -> continuation.resumeWithException(error) }
    addOnCanceledListener { continuation.cancel() }
}