package com.korbuts.spigun.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String,
    val name: String
)

@Serializable
data class PlayerGroup(
    val id: String,
    val name: String,
    val players: List<Player>
)

@Serializable
data class TopicsPack(
    val id: String,
    val name: String,
    val words: List<String>,
    val isCustom: Boolean = false
)

@Serializable
data class RoundConfig(
    val roundDurationMinutes: Int = 5,
    val spyCount: Int = 1,
    val activeTopicsPackIds: List<String> = emptyList(),
    val activePlayerIds: List<String> = emptyList()
)

enum class PlayerRole {
    PLAYER, SPY
}

@Serializable
data class GameState(
    val assignedTopic: String,
    val playerRoles: Map<String, PlayerRole>, // Player ID to Role
    val remainingTimeSeconds: Long,
    val isCardRevealed: Boolean = false
)
