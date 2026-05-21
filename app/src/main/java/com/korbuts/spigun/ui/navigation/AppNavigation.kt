package com.korbuts.spigun.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.korbuts.spigun.ui.screens.home.HomeScreen
import com.korbuts.spigun.ui.screens.players.PlayerManagementScreen
import com.korbuts.spigun.ui.screens.topics.TopicsManagementScreen
import com.korbuts.spigun.ui.screens.setup.GameSetupScreen
import com.korbuts.spigun.ui.screens.game.GamePlayScreen
import com.korbuts.spigun.ui.screens.game.GamePlayViewModel
import com.korbuts.spigun.ui.screens.game.GameTimerScreen
import com.korbuts.spigun.ui.screens.game.ResultsScreen

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
            TopicsManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.GameSetup> {
            GameSetupScreen(
                onBack = { navController.popBackStack() },
                onManageGroups = { navController.navigate(Screen.PlayerManagement) },
                onStartGame = { navController.navigate(Screen.GameGraph) }
            )
        }

        navigation<Screen.GameGraph>(startDestination = Screen.RoleReveal) {
            composable<Screen.RoleReveal> {
                val backStackEntry = remember(it) {
                    navController.getBackStackEntry<Screen.GameGraph>()
                }
                val viewModel: GamePlayViewModel = hiltViewModel(backStackEntry)
                
                BackHandler {
                    navController.popBackStack<Screen.GameSetup>(inclusive = false)
                }

                GamePlayScreen(
                    onStartDiscussion = { navController.navigate(Screen.GameTimer) },
                    viewModel = viewModel
                )
            }

            composable<Screen.GameTimer> {
                val backStackEntry = remember(it) {
                    navController.getBackStackEntry<Screen.GameGraph>()
                }
                val viewModel: GamePlayViewModel = hiltViewModel(backStackEntry)
                
                BackHandler {
                    navController.popBackStack<Screen.GameSetup>(inclusive = false)
                }

                GameTimerScreen(
                    onFinish = { navController.navigate(Screen.Results) },
                    viewModel = viewModel
                )
            }

            composable<Screen.Results> {
                val backStackEntry = remember(it) {
                    navController.getBackStackEntry<Screen.GameGraph>()
                }
                val viewModel: GamePlayViewModel = hiltViewModel(backStackEntry)
                
                BackHandler {
                    navController.popBackStack<Screen.GameSetup>(inclusive = false)
                }

                ResultsScreen(
                    onPlayAgain = {
                        navController.popBackStack<Screen.GameSetup>(inclusive = false)
                    },
                    onBackHome = {
                        navController.popBackStack<Screen.Home>(inclusive = false)
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}
