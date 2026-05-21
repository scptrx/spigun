package com.korbuts.spigun.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Player(
    val id: String,
    val name: String
) : Parcelable

@Serializable
@Parcelize
data class PlayerGroup(
    val id: String,
    val name: String,
    val players: List<Player>
) : Parcelable

@Serializable
@Parcelize
data class TopicsPack(
    val id: String,
    val name: String,
    val words: List<String>,
    val isCustom: Boolean = false
) : Parcelable

@Serializable
@Parcelize
data class RoundConfig(
    val roundDurationMinutes: Int = 5,
    val spyCount: Int = 1,
    val activeTopicsPackIds: List<String> = emptyList(),
    val activePlayerIds: List<String> = emptyList()
) : Parcelable

enum class PlayerRole {
    PLAYER, SPY
}

@Serializable
@Parcelize
data class GameState(
    val assignedTopic: String,
    val playerRoles: Map<String, PlayerRole>,
    val remainingTimeSeconds: Long,
    val isCardRevealed: Boolean = false
) : Parcelable
