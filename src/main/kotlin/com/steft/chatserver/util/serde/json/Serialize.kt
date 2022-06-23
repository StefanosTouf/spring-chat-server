@file:Suppress("ComplexRedundantLet")

package com.steft.chatserver.util.serde.json

import com.steft.chatserver.model.Serialized
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import reactor.core.publisher.Flux

inline fun <reified T> serialize(nonSerializedFlux: Flux<T>): Flux<Serialized<T>> =
    nonSerializedFlux
        .map { nonSerialized ->
            Json.encodeToString(nonSerialized)
                .let { Serialized(it) }
        }