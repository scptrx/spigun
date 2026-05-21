package com.korbuts.spigun.data.local.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(tableName = "player_groups")
data class PlayerGroupEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(
    tableName = "group_player_cross_ref",
    primaryKeys = ["groupId", "playerId"]
)
data class GroupPlayerCrossRef(
    val groupId: String,
    val playerId: String
)

data class GroupWithPlayers(
    @Embedded val group: PlayerGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            GroupPlayerCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "playerId"
        )
    )
    val players: List<PlayerEntity>
)

@Entity(tableName = "topic_packs")
data class TopicPackEntity(
    @PrimaryKey val id: String,
    val name: String,
    val words: String,
    val isCustom: Boolean
)
