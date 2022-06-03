package com.steft.chatserver.util.serde.deserialize

import com.steft.chatserver.model.Serialized
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import reactor.core.publisher.Flux
import java.io.ByteArrayInputStream
import java.io.DataInputStream

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> deserialize(serialized: Serialized<T>): T =
    serialized.let { (data) ->
        ByteArrayInputStream(data)
            .let { Json.decodeFromStream(it) }
    }