package com.slh.studentlabhub.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.di.AppContainer
import com.slh.studentlabhub.ui.components.SlhBottomBar
import com.slh.studentlabhub.ui.screens.archive.ArchiveRoute
import com.slh.studentlabhub.ui.screens.auth.LoginRoute
import com.slh.studentlabhub.ui.screens.labdetails.LabDetailsRoute
import com.slh.studentlabhub.ui.screens.lablist.LabListRoute
import com.slh.studentlabhub.ui.screens.reference.ReferenceCategoryRoute
import com.slh.studentlabhub.ui.screens.reference.ReferenceRoute
import com.slh.studentlabhub.ui.screens.settings.SettingsRoute
import com.slh.studentlabhub.ui.screens.statistics.StatisticsRoute
import com.slh.studentlabhub.ui.screens.subjects.SubjectsRoute
import com.slh.studentlabhub.ui.screens.today.TodayRoute
import com.slh.studentlabhub.ui.theme.SlhBackground
import com.slh.studentlabhub.ui.screens.schedule.ScheduleRoute

@Composable
fun SlhApp() {
    val context = LocalContext.current
    val container = remember(context) { (context.applicationContext as SlhApplication).container }
    val homeRoute = remember { container.settingsRepository.settings.value.startScreen.route }
    val startDestination = remember {
        if (container.classroomRepository.signedInEmail != null) homeRoute else Routes.LOGIN
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var lastTab by remember { mutableStateOf<BottomTab?>(null) }
    val currentTab = when (currentRoute) {
        null, Routes.LOGIN -> null
        else -> BottomTab.fromRoute(currentRoute) ?: lastTab ?: BottomTab.fromPrefix(currentRoute)
    }
    SideEffect {
        if (lastTab != currentTab) lastTab = currentTab
    }

    NotificationPermissionPrompt(
        active = currentTab != null,
        container = container,
    )

    val pendingAction by LaunchActions.pending.collectAsState()
    LaunchedEffect(pendingAction, currentTab != null) {
        val action = pendingAction ?: return@LaunchedEffect
        if (currentTab == null) return@LaunchedEffect
        LaunchActions.pending.value = null
        val target = when (action) {
            LaunchActions.TODAY -> Routes.TODAY
            LaunchActions.REFERENCE -> Routes.REFERENCE
            else -> Routes.SUBJECTS
        }
        if (action == LaunchActions.ADD_SUBJECT) {
            LaunchActions.addSubjectRequested.value = true
        }
        navController.navigate(target) {
            popUpTo(homeRoute) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        containerColor = SlhBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentTab != null) {
                SlhBottomBar(
                    tabs = BottomTab.entries,
                    selected = currentTab,
                    onTabClick = { tab ->
                        if (tab == currentTab) {
                            navController.popBackStack(tab.route, inclusive = false)
                        } else {
                            navController.navigate(tab.route) {
                                popUpTo(homeRoute) { saveState = true }
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.LOGIN) {
                LoginRoute(
                    onLoggedIn = {
                        navController.navigate(homeRoute) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.TODAY) {
                TodayRoute(
                    onOpenLab = { navController.navigate(Routes.labDetails(it)) },
                    onOpenSchedule = { navController.navigate(Routes.SCHEDULE) },
                    onOpenSubject = { navController.navigate(Routes.subject(it)) },
                    onOpenSettings = {
                        navController.navigate(Routes.SETTINGS) {
                            popUpTo(homeRoute) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenSubjects = {
                        navController.navigate(Routes.SUBJECTS) {
                            popUpTo(homeRoute) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(Routes.SCHEDULE) {
                ScheduleRoute(
                    onBack = { navController.popBackStack() },
                    onOpenSubject = { navController.navigate(Routes.subject(it)) },
                    onOpenSettings = {
                        navController.navigate(Routes.SETTINGS) {
                            popUpTo(homeRoute) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(Routes.STATISTICS) {
                StatisticsRoute(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.SUBJECTS) {
                SubjectsRoute(
                    onSubjectClick = { navController.navigate(Routes.subject(it)) },
                    onOpenStatistics = { navController.navigate(Routes.STATISTICS) },
                    onOpenArchive = { navController.navigate(Routes.ARCHIVE) },
                )
            }
            composable(Routes.ARCHIVE) {
                ArchiveRoute(
                    onBack = { navController.popBackStack() },
                    onOpenSubject = { navController.navigate(Routes.subject(it)) },
                    onOpenCourse = { },
                )
            }
            composable(
                route = Routes.SUBJECT,
                arguments = listOf(
                    navArgument(Routes.ARG_SUBJECT_ID) { type = NavType.LongType },
                ),
            ) {
                LabListRoute(
                    onBack = { navController.popBackStack() },
                    onLabClick = { lab -> navController.navigate(Routes.labDetails(lab.id)) },
                )
            }
            composable(
                route = Routes.LAB_DETAILS,
                arguments = listOf(
                    navArgument(Routes.ARG_LAB_ID) { type = NavType.LongType },
                ),
            ) {
                LabDetailsRoute(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.REFERENCE) {
                ReferenceRoute(
                    onCategoryClick = { category ->
                        navController.navigate(Routes.referenceCategory(category.id))
                    },
                )
            }
            composable(
                route = Routes.REFERENCE_CATEGORY,
                arguments = listOf(
                    navArgument(Routes.ARG_REF_CATEGORY_ID) { type = NavType.StringType },
                ),
            ) {
                ReferenceCategoryRoute(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.SETTINGS) {
                SettingsRoute(
                    onSignedOut = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun NotificationPermissionPrompt(
    active: Boolean,
    container: AppContainer,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { }
    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        val repository = container.settingsRepository
        val settings = repository.settings.value
        if (!settings.remindersEnabled && !settings.redoAlerts) return@LaunchedEffect
        if (repository.notificationPromptShown) return@LaunchedEffect
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) return@LaunchedEffect
        repository.notificationPromptShown = true
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
