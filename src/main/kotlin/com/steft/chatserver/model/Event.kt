package com.steft.chatserver.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed class Event {
    abstract val eventId: EventId
    abstract val to: UserId
    abstract val from: UserId

    @Serializable
    @SerialName("ACK")
    data class Ack(
        override val eventId: EventId = EventId(UUID.randomUUID()),
        override val to: UserId,
        override val from: UserId,
        val body: EventId) : Event()

    @Serializable
    @SerialName("MESSAGE")
    data class Message(
        override val eventId: EventId = EventId(UUID.randomUUID()),
        override val to: UserId,
        override val from: UserId,
        val body: String) : Event()
}
