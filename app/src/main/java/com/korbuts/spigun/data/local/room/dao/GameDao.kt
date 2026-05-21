package com.korbuts.spigun.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.korbuts.spigun.data.local.room.entities.GroupPlayerCrossRef
import com.korbuts.spigun.data.local.room.entities.GroupWithPlayers
import com.korbuts.spigun.data.local.room.entities.PlayerEntity
import com.korbuts.spigun.data.local.room.entities.PlayerGroupEntity
import com.korbuts.spigun.data.local.room.entities.TopicPackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Query("SELECT * FROM players")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: PlayerGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPlayerCrossRef(crossRef: GroupPlayerCrossRef)

    @Transaction
    @Query("SELECT * FROM player_groups")
    fun getAllGroupsWithPlayers(): Flow<List<GroupWithPlayers>>

    @Query("DELETE FROM group_player_cross_ref WHERE groupId = :groupId AND playerId = :playerId")
    suspend fun deletePlayerFromGroup(groupId: String, playerId: String)

    @Query("""
        SELECT players.* FROM players 
        INNER JOIN group_player_cross_ref ON players.id = group_player_cross_ref.playerId 
        WHERE group_player_cross_ref.groupId = :groupId
    """)
    fun getPlayersForGroup(groupId: String): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopicPack(topicPack: TopicPackEntity)

    @Query("SELECT * FROM topic_packs")
    fun getAllTopicPacks(): Flow<List<TopicPackEntity>>

    @Query("DELETE FROM players WHERE id = :playerId")
    suspend fun deletePlayer(playerId: String)

    @Query("DELETE FROM player_groups WHERE id = :groupId")
    suspend fun deleteGroup(groupId: String)

    @Query("DELETE FROM group_player_cross_ref WHERE playerId = :playerId")
    suspend fun deletePlayerFromAllGroups(playerId: String)

    @Query("DELETE FROM group_player_cross_ref WHERE groupId = :groupId")
    suspend fun deleteAllPlayersFromGroup(groupId: String)
}
