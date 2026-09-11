package lat.virgotp.fondly.feature.balances

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import lat.virgotp.fondly.feature.balances.createbalance.CreateBalanceScreen
import lat.virgotp.fondly.feature.balances.dashboard.DashboardScreen

private object BalancesRoutes {
    const val DASHBOARD = "dashboard"
    const val CREATE_BALANCE = "create_balance"
}

@Composable
fun BalancesNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = BalancesRoutes.DASHBOARD) {
        composable(BalancesRoutes.DASHBOARD) {
            DashboardScreen(
                onCreateBalanceClick = { navController.navigate(BalancesRoutes.CREATE_BALANCE) }
            )
        }
        composable(BalancesRoutes.CREATE_BALANCE) {
            CreateBalanceScreen(
                onSaved = { navController.popBackStack() }
            )
        }
    }
}