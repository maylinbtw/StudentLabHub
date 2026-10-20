package com.slh.studentlabhub.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.slh.studentlabhub.ui.components.SlhBottomBar
import com.slh.studentlabhub.ui.screens.auth.LoginScreen
import com.slh.studentlabhub.ui.screens.auth.LoginUiState
import com.slh.studentlabhub.ui.screens.deadlines.DeadlinesScreen
import com.slh.studentlabhub.ui.screens.deadlines.sampleCourses
import com.slh.studentlabhub.ui.screens.labs.LabsRoute
import com.slh.studentlabhub.ui.screens.placeholder.PlaceholderScreen
import com.slh.studentlabhub.ui.theme.SlhBlack
import kotlinx.coroutines.delay

@Composable
fun SlhApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = BottomTab.fromRoute(backStackEntry?.destination?.route)

    Scaffold(
        containerColor = SlhBlack,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentTab != null) {
                SlhBottomBar(
                    tabs = BottomTab.entries,
                    selected = currentTab,
                    onTabClick = { tab ->
                        if (tab != currentTab) {
                            navController.navigate(tab.route) {
                                popUpTo(Routes.DEADLINES) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.LOGIN) {
                LoginRoute(
                    onLoggedIn = {
                        navController.navigate(Routes.DEADLINES) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.DEADLINES) {
                DeadlinesScreen(
                    courses = sampleCourses,
                    onCourseClick = {},
                    onSyncClick = {},
                )
            }
            composable(Routes.LABS) {
                LabsRoute(
                    onSubjectClick = {},
                    onStatisticsClick = {},
                )
            }
            composable(Routes.REFERENCE) {
                PlaceholderScreen(overline = "Offline · SQLite", title = "СПРАВОЧНИК")
            }
            composable(Routes.CALENDAR) {
                PlaceholderScreen(overline = "Ручная синхронизация", title = "КАЛЕНДАРЬ")
            }
            composable(Routes.SETTINGS) {
                PlaceholderScreen(overline = "Авторизация / Офлайн-режим / Уведомления", title = "НАСТРОЙКИ")
            }
        }
    }
}

@Composable
private fun LoginRoute(onLoggedIn: () -> Unit) {
    var state by remember { mutableStateOf(LoginUiState()) }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            delay(1500)
            state = state.copy(isLoading = false, accountEmail = "student@gmail.com")
            onLoggedIn()
        }
    }

    LoginScreen(
        state = state,
        onSignInClick = { state = state.copy(isLoading = true, errorMessage = null) },
        onChooseAnotherAccountClick = { state = LoginUiState() },
        onTermsClick = {},
    )
}