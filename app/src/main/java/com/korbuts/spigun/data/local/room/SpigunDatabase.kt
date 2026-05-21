package com.korbuts.spigun.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.korbuts.spigun.data.local.room.dao.GameDao
import com.korbuts.spigun.data.local.room.entities.GroupPlayerCrossRef
import com.korbuts.spigun.data.local.room.entities.PlayerEntity
import com.korbuts.spigun.data.local.room.entities.PlayerGroupEntity
import com.korbuts.spigun.data.local.room.entities.TopicPackEntity

@Database(
    entities = [
        PlayerEntity::class,
        PlayerGroupEntity::class,
        GroupPlayerCrossRef::class,
        TopicPackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SpigunDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
