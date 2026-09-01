package com.watchtower.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.watchtower.app.WatchtowerApplication
import com.watchtower.app.ui.detail.DetailScreen
import com.watchtower.app.ui.digest.DigestHistoryScreen
import com.watchtower.app.ui.home.HomeScreen
import com.watchtower.app.ui.settings.SettingsScreen
import kotlinx.coroutines.flow.first

private object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val DIGESTS = "digests"
    const val DETAIL = "detail/{ticker}"
    fun detail(ticker: String) = "detail/$ticker"
}

@Composable
fun WatchtowerNavHost() {
    val app = LocalContext.current.applicationContext as WatchtowerApplication
    val navController = rememberNavController()

    // Route to Settings first if no base URL/API key has been saved yet.
    var startDestination by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val settings = app.settingsRepository.settingsFlow.first()
        startDestination = if (settings.isConfigured) Routes.HOME else Routes.SETTINGS
    }

    val resolvedStart = startDestination ?: return

    NavHost(navController = navController, startDestination = resolvedStart) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenTicker = { ticker -> navController.navigate(Routes.detail(ticker)) },
                onOpenDigests = { navController.navigate(Routes.DIGESTS) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onSaved = {
                    navController.navigate(Routes.HOME) {
                        // Clears the whole back stack (using the graph's own id, since
                        // routes here are String-based, not Int destination ids) so
                        // Settings never reappears on back-press after a successful save.
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                // No back option when Settings is the forced first-run screen
                // (nothing pushed it, so there's nothing to go back to) —
                // only shown when opened via Home's gear icon.
                onBack = if (navController.previousBackStackEntry != null) {
                    { navController.popBackStack() }
                } else {
                    null
                },
            )
        }
        composable(Routes.DIGESTS) {
            DigestHistoryScreen(
                onBack = { navController.popBackStack() },
                onOpenTicker = { ticker -> navController.navigate(Routes.detail(ticker)) },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("ticker") { type = NavType.StringType }),
        ) { backStackEntry ->
            val ticker = backStackEntry.arguments?.getString("ticker").orEmpty()
            DetailScreen(ticker = ticker, onBack = { navController.popBackStack() })
        }
    }
}
