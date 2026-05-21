package com.korbuts.spigun.data.local.datastore

import androidx.datastore.core.Serializer
import com.korbuts.spigun.data.model.RoundConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object RoundConfigSerializer : Serializer<RoundConfig> {
    override val defaultValue: RoundConfig = RoundConfig()

    override suspend fun readFrom(input: InputStream): RoundConfig {
        return try {
            Json.decodeFromString(
                deserializer = RoundConfig.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: RoundConfig, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = RoundConfig.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}
