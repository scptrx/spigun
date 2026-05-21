package com.korbuts.spigun.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.room.Room
import com.korbuts.spigun.data.local.datastore.RoundConfigSerializer
import com.korbuts.spigun.data.local.room.SpigunDatabase
import com.korbuts.spigun.data.local.room.dao.GameDao
import com.korbuts.spigun.data.model.RoundConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.roundConfigDataStore: DataStore<RoundConfig> by dataStore(
    fileName = "round_config.json",
    serializer = RoundConfigSerializer
)

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpigunDatabase {
        return Room.databaseBuilder(
            context,
            SpigunDatabase::class.java,
            "spigun_db"
        ).build()
    }

    @Provides
    fun provideGameDao(database: SpigunDatabase): GameDao {
        return database.gameDao()
    }

    @Provides
    @Singleton
    fun provideRoundConfigDataStore(@ApplicationContext context: Context): DataStore<RoundConfig> {
        return context.roundConfigDataStore
    }
}
