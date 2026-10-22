package com.slh.studentlabhub.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.ui.components.SlhBottomBar
import com.slh.studentlabhub.ui.screens.auth.LoginRoute
import com.slh.studentlabhub.ui.screens.coursetasks.CourseTasksRoute
import com.slh.studentlabhub.ui.screens.deadlines.DeadlinesRoute
import com.slh.studentlabhub.ui.screens.labdetails.LabDetailsRoute
import com.slh.studentlabhub.ui.screens.lablist.LabListRoute
import com.slh.studentlabhub.ui.screens.labs.LabsRoute
import com.slh.studentlabhub.ui.screens.placeholder.PlaceholderScreen
import com.slh.studentlabhub.ui.screens.reference.ReferenceCategoryRoute
import com.slh.studentlabhub.ui.screens.reference.ReferenceRoute
import com.slh.studentlabhub.ui.screens.statistics.StatisticsRoute
import com.slh.studentlabhub.ui.theme.SlhBlack

@Composable
fun SlhApp() {
    val context = LocalContext.current
    val startDestination = remember {
        val container = (context.applicationContext as SlhApplication).container
        if (container.classroomRepository.signedInEmail != null) Routes.DEADLINES else Routes.LOGIN
    }

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
                        if (tab == currentTab) {
                            navController.popBackStack(tab.route, inclusive = false)
                        } else {
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
            startDestination = startDestination,
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
                DeadlinesRoute(
                    onOpenCourse = { courseId ->
                        navController.navigate(Routes.courseTasks(courseId))
                    },
                )
            }
            composable(
                route = Routes.COURSE_TASKS,
                arguments = listOf(
                    navArgument(Routes.ARG_COURSE_ID) { type = NavType.StringType },
                ),
            ) {
                CourseTasksRoute(
                    onBack = { navController.popBackStack() },
                    onOpenSubject = { subjectId ->
                        navController.navigate(Routes.labList(subjectId))
                    },
                )
            }
            composable(Routes.LABS) {
                LabsRoute(
                    onSubjectClick = { subject ->
                        navController.navigate(Routes.labList(subject.id))
                    },
                    onStatisticsClick = {
                        navController.navigate(Routes.STATISTICS)
                    },
                )
            }
            composable(Routes.STATISTICS) {
                StatisticsRoute(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.LAB_LIST,
                arguments = listOf(
                    navArgument(Routes.ARG_SUBJECT_ID) { type = NavType.LongType },
                ),
            ) {
                LabListRoute(
                    onBack = { navController.popBackStack() },
                    onLabClick = { lab ->
                        navController.navigate(Routes.labDetails(lab.id))
                    },
                    onOpenCourse = { courseId ->
                        navController.navigate(Routes.courseTasks(courseId))
                    },
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
            composable(Routes.CALENDAR) {
                PlaceholderScreen(overline = "Дедлайны по датам", title = "КАЛЕНДАРЬ")
            }
            composable(Routes.SETTINGS) {
                PlaceholderScreen(overline = "Аккаунт и приложение", title = "НАСТРОЙКИ")
            }
        }
    }
}
