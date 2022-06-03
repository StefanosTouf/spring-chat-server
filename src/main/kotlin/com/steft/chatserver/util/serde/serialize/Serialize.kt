@file:Suppress("ComplexRedundantLet")

package com.steft.chatserver.util.serde.serialize

import com.steft.chatserver.model.Serialized
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> serialize(t: T): Serialized<T> =
    Json.encodeToString(t)
        .let { Serialized(it.toByteArray()) }