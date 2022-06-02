package com.steft.chatserver.model

import com.steft.chatserver.service.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@JvmInline
value class RoutingKey(val string: String)

@Serializable
@JvmInline
value class UserId(val string: String)

@Serializable
@JvmInline
value class MessageId(@Serializable(with = UUIDSerializer::class) val id: UUID)

@Serializable
@JvmInline
value class Serialized<T>(val data: ByteArray)
