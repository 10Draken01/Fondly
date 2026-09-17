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
import lat.virgotp.fondly.sections.BalanceSectionsScreen
import lat.virgotp.fondly.balances.createbalance.CreateBalanceScreen
import lat.virgotp.fondly.balances.dashboard.DashboardScreen
import lat.virgotp.fondly.balances.detail.BalanceDetailScreen
import lat.virgotp.fondly.balances.editbalance.EditBalanceScreen
import lat.virgotp.fondly.settings.SettingsScreen
import lat.virgotp.fondly.ui_common.templates.FondlyBottomNavigation
import lat.virgotp.fondly.ui_common.templates.FondlySection
import lat.virgotp.fondly.ui_common.templates.FondlySplashScreen

@Composable
fun FondlyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            val splashViewModel: SplashViewModel = hiltViewModel()
            FondlySplashScreen(onFinished = {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("main") { FondlyMainScreen() }
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
                        popUpTo(FondlySection.BALANCES.route) {
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
            NavHost(navController = navController, startDestination = FondlySection.BALANCES.route) {
                composable("balances") {
                    DashboardScreen(
                        onCreateBalanceClick = { navController.navigate("create") },
                        onBalanceClick = { id -> navController.navigate("detail/$id") },
                        onEditClick = { id -> navController.navigate("edit/$id") }
                    )
                }
                composable("balance_sections") {
                    BalanceSectionsScreen(
                        onBalanceClick = { id -> navController.navigate("detail/$id") },
                        onEditClick = { id -> navController.navigate("edit/$id") }
                    )
                }
                composable("settings") { SettingsScreen() }

                composable(
                    route = "detail/{balanceId}",
                    arguments = listOf(navArgument("balanceId") { type = NavType.LongType })
                ) { entry ->
                    val id = entry.arguments?.getLong("balanceId") ?: return@composable
                    BalanceDetailScreen(
                        balanceId = id,
                        onEditClick = { navController.navigate("edit/$id") },
                        onAddChildClick = { navController.navigate("create?parentId=$id") },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "create?parentId={parentId}",
                    arguments = listOf(
                        navArgument("parentId") { type = NavType.LongType; defaultValue = -1L }
                    )
                ) { entry ->
                    CreateBalanceScreen(
                        parentBalanceId = entry.arguments?.getLong("parentId")?.takeIf { it != -1L },
                        onSaved = { navController.popBackStack() }
                    )
                }
                composable(
                    route = "edit/{balanceId}",
                    arguments = listOf(navArgument("balanceId") { type = NavType.LongType })
                ) { entry ->
                    val id = entry.arguments?.getLong("balanceId") ?: return@composable
                    EditBalanceScreen(
                        balanceId = id,
                        onSaved = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}