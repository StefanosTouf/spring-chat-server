package com.steft.chatserver.model

import com.steft.chatserver.util.serde.json.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@JvmInline
value class EventId(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID)
