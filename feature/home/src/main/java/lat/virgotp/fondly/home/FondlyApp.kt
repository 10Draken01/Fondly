package lat.virgotp.fondly.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import lat.virgotp.fondly.balance_sections.BalanceSectionsScreen
import lat.virgotp.fondly.balances.createbalance.CreateBalanceScreen
import lat.virgotp.fondly.balances.dashboard.DashboardScreen
import lat.virgotp.fondly.balances.detail.BalanceDetailScreen
import lat.virgotp.fondly.balances.editbalance.EditBalanceScreen
import lat.virgotp.fondly.settings.SettingsScreen
import lat.virgotp.fondly.ui_common.navigation.FondlyRoutes
import lat.virgotp.fondly.ui_common.templates.FondlyBottomNavigation
import lat.virgotp.fondly.ui_common.templates.FondlySection
import lat.virgotp.fondly.ui_common.templates.FondlySplashScreen

@Composable
fun FondlyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = FondlyRoutes.SPLASH) {
        composable(FondlyRoutes.SPLASH) {
            // Instanciarlo dispara ProcessBalanceRenewalsUseCase durante el splash.
            hiltViewModel<SplashViewModel>()
            FondlySplashScreen(onFinished = {
                navController.navigate(FondlyRoutes.MAIN) {
                    popUpTo(FondlyRoutes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(FondlyRoutes.MAIN) { FondlyMainScreen() }
    }
}

@Composable
fun FondlyMainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val scheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            FondlyBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(FondlyRoutes.BALANCES) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(scheme.background, scheme.surface)))
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(scheme.primary.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(size.width / 2f, -size.height * 0.05f),
                            radius = size.width * 0.95f
                        )
                    )
                }
        ) {
            NavHost(navController = navController, startDestination = FondlyRoutes.BALANCES) {
                composable(FondlyRoutes.BALANCES) {
                    DashboardScreen(
                        onCreateBalanceClick = { navController.navigate(FondlyRoutes.create()) },
                        onBalanceClick = { id -> navController.navigate(FondlyRoutes.detail(id)) },
                        onEditClick = { id -> navController.navigate(FondlyRoutes.edit(id)) }
                    )
                }
                composable(FondlyRoutes.BALANCE_SECTIONS) {
                    BalanceSectionsScreen(
                        onBalanceClick = { id -> navController.navigate(FondlyRoutes.detail(id)) },
                        onEditClick = { id -> navController.navigate(FondlyRoutes.edit(id)) }
                    )
                }
                composable(FondlyRoutes.SETTINGS) { SettingsScreen() }

                composable(
                    route = FondlyRoutes.DETAIL_PATTERN,
                    arguments = listOf(navArgument(FondlyRoutes.ARG_BALANCE_ID) { type = NavType.LongType })
                ) { entry ->
                    val id = entry.arguments?.getLong(FondlyRoutes.ARG_BALANCE_ID) ?: return@composable
                    BalanceDetailScreen(
                        balanceId = id,
                        onEditClick = { navController.navigate(FondlyRoutes.edit(id)) },
                        onAddChildClick = { navController.navigate(FondlyRoutes.create(parentId = id)) },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = FondlyRoutes.CREATE_PATTERN,
                    arguments = listOf(
                        navArgument(FondlyRoutes.ARG_PARENT_ID) {
                            type = NavType.LongType
                            defaultValue = FondlyRoutes.NO_PARENT
                        }
                    )
                ) { entry ->
                    CreateBalanceScreen(
                        parentBalanceId = entry.arguments?.getLong(FondlyRoutes.ARG_PARENT_ID)
                            ?.takeIf { it != FondlyRoutes.NO_PARENT },
                        onSaved = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = FondlyRoutes.EDIT_PATTERN,
                    arguments = listOf(navArgument(FondlyRoutes.ARG_BALANCE_ID) { type = NavType.LongType })
                ) { entry ->
                    val id = entry.arguments?.getLong(FondlyRoutes.ARG_BALANCE_ID) ?: return@composable
                    EditBalanceScreen(
                        balanceId = id,
                        onSaved = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
