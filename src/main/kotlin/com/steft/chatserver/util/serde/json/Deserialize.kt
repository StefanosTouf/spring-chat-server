package com.steft.chatserver.util.serde.json

import com.steft.chatserver.model.Serialized
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

inline fun <reified T> deserialize(serialized: Flux<Serialized<T>>): Flux<T> =
    serialized
        .map { it.data }
        .transform(::deserialize)

@JvmName("deserializeDirect")
inline fun <reified T> deserialize(serialized: Flux<String>): Flux<T> =
    serialized.flatMap { data ->
        Mono.fromCallable {
            Json.decodeFromString(data)
        }
    }

