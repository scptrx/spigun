package com.korbuts.spigun.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.korbuts.spigun.ui.screens.home.HomeScreen
import com.korbuts.spigun.ui.screens.groups.GroupsScreen
import com.korbuts.spigun.ui.screens.groups.AddGroupScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onStartNewGame = { navController.navigate(Screen.Lobby()) },
                onManageGroups = { navController.navigate(Screen.Groups(id = 0)) },
                onBrowseTopics = { navController.navigate(Screen.Topics(id = 0)) }
            )
        }

        composable<Screen.Groups> { backStackEntry ->
            val route: Screen.Groups = backStackEntry.toRoute()
            GroupsScreen(
                onAddGroup = { navController.navigate(Screen.AddGroup(id = route.id)) },
                onSelectGroup = { groupId -> navController.navigate(Screen.Lobby(groupId)) }
            )
        }

        composable<Screen.AddGroup> {
            AddGroupScreen(
                onGroupSaved = { navController.popBackStack() }
            )
        }

        composable<Screen.Topics> {
            // TODO: Implement ThemesScreen
        }

        composable<Screen.Lobby> {
            // TODO: Implement LobbyScreen
        }

        composable<Screen.Game> {
            // TODO: Implement GameScreen
        }
    }
}
