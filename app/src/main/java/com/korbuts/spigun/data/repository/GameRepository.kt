package com.korbuts.spigun.data.repository

import androidx.datastore.core.DataStore
import com.korbuts.spigun.data.local.room.dao.GameDao
import com.korbuts.spigun.data.local.room.entities.GroupPlayerCrossRef
import com.korbuts.spigun.data.local.room.entities.PlayerEntity
import com.korbuts.spigun.data.local.room.entities.PlayerGroupEntity
import com.korbuts.spigun.data.local.room.entities.TopicPackEntity
import com.korbuts.spigun.data.model.Player
import com.korbuts.spigun.data.model.PlayerGroup
import com.korbuts.spigun.data.model.RoundConfig
import com.korbuts.spigun.data.model.TopicsPack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val roundConfigDataStore: DataStore<RoundConfig>
) {
    suspend fun savePlayer(player: Player) {
        gameDao.insertPlayer(PlayerEntity(player.id, player.name))
    }

    fun getAllPlayers(): Flow<List<Player>> = gameDao.getAllPlayers().map { entities ->
        entities.map { Player(it.id, it.name) }
    }

    suspend fun saveGroup(group: PlayerGroup) {
        gameDao.insertGroup(PlayerGroupEntity(group.id, group.name))
        gameDao.deleteAllPlayersFromGroup(group.id)
        group.players.forEach { player ->
            gameDao.insertPlayer(PlayerEntity(player.id, player.name))
            gameDao.insertGroupPlayerCrossRef(GroupPlayerCrossRef(group.id, player.id))
        }
    }

    fun getGroups(): Flow<List<PlayerGroup>> = gameDao.getAllGroupsWithPlayers().map { relationList ->
        relationList.map { relation ->
            PlayerGroup(
                id = relation.group.id,
                name = relation.group.name,
                players = relation.players.map { Player(it.id, it.name) }
            )
        }
    }

    suspend fun removePlayerFromGroup(groupId: String, playerId: String) {
        gameDao.deletePlayerFromGroup(groupId, playerId)
    }

    suspend fun deletePlayer(playerId: String) {
        gameDao.deletePlayer(playerId)
        gameDao.deletePlayerFromAllGroups(playerId)
    }

    suspend fun deleteGroup(groupId: String) {
        gameDao.deleteGroup(groupId)
        gameDao.deleteAllPlayersFromGroup(groupId)
    }

    suspend fun saveTopicPack(pack: TopicsPack) {
        gameDao.insertTopicPack(
            TopicPackEntity(
                id = pack.id,
                name = pack.name,
                words = pack.words.joinToString(","),
                isCustom = pack.isCustom
            )
        )
    }

    fun getTopicPacks(): Flow<List<TopicsPack>> = gameDao.getAllTopicPacks().map { entities ->
        entities.map { entity ->
            TopicsPack(
                id = entity.id,
                name = entity.name,
                words = entity.words.split(","),
                isCustom = entity.isCustom
            )
        }
    }

    val roundConfig: Flow<RoundConfig> = roundConfigDataStore.data

    suspend fun updateRoundConfig(update: (RoundConfig) -> RoundConfig) {
        roundConfigDataStore.updateData { currentConfig ->
            update(currentConfig)
        }
    }
}
