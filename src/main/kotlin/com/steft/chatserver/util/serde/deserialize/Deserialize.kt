package com.steft.chatserver.util.serde.deserialize

import com.steft.chatserver.model.Serialized
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

inline fun <reified T> deserialize(serialized: Serialized<T>): T =
    serialized.let { (data) ->
        Json.decodeFromString(data)
    }