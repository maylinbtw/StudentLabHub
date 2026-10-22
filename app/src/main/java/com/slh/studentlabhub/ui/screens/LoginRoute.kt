package com.slh.studentlabhub.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slh.studentlabhub.ui.auth.rememberGoogleAuthorization

@Composable
fun LoginRoute(
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoggedIn()
        }
    }

    val authorize = rememberGoogleAuthorization(
        onAuthorized = viewModel::onAuthorized,
        onError = viewModel::onAuthFailed,
    )

    LoginScreen(
        state = state,
        onSignInClick = {
            viewModel.onAuthStarted()
            authorize()
        },
        onChooseAnotherAccountClick = {
            viewModel.switchAccount(onReady = authorize)
        },
        onTermsClick = {},
        modifier = modifier,
    )
}