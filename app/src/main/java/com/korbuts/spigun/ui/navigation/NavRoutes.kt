package com.korbuts.spigun.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    object Home : Screen()

    @Serializable
    data class Groups(val id: Int) : Screen()

    @Serializable
    data class AddGroup(val id: Int) : Screen()

    @Serializable
    data class Topics(val id: Int) : Screen()

    @Serializable
    data class Lobby(val groupId: Long? = null) : Screen()

    @Serializable
    data class Game(val chatId: Long) : Screen()
}
