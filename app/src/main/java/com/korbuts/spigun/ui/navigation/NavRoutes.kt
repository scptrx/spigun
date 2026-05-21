package com.korbuts.spigun.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object PlayerManagement : Screen

    @Serializable
    data object TopicManagement : Screen

    @Serializable
    data object GameSetup : Screen

    @Serializable
    data object GamePlay : Screen

    @Serializable
    data object Results : Screen
}
