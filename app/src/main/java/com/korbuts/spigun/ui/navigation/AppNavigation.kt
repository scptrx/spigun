package com.korbuts.spigun.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.korbuts.spigun.ui.screens.home.HomeScreen
import com.korbuts.spigun.ui.screens.players.PlayerManagementScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onStartNewGame = { navController.navigate(Screen.GameSetup) },
                onManageGroups = { navController.navigate(Screen.PlayerManagement) },
                onBrowseTopics = { navController.navigate(Screen.TopicManagement) }
            )
        }

        composable<Screen.PlayerManagement> {
            PlayerManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.TopicManagement> {
            // TODO: Implement Topic Pack Browsing
            Text("Topic Management Screen")
        }

        composable<Screen.GameSetup> {
            // TODO: Implement Round Configuration
            Text("Game Setup Screen")
        }

        composable<Screen.GamePlay> {
            // TODO: Implement Game Role Reveal & Timer
            Text("Game Play Screen")
        }

        composable<Screen.Results> {
            // TODO: Implement Voting & Results
            Text("Results Screen")
        }
    }
}
