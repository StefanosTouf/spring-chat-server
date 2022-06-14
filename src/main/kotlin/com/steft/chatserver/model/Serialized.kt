package com.steft.chatserver.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Serialized<T>(val data: String) {
    operator fun component1(): String = data
}
