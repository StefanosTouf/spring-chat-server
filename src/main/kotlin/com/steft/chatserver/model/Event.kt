package com.steft.chatserver.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventId: EventId,
    val from: UserId,
    val untagged: UntaggedEvent)

@Serializable
sealed class UntaggedEvent {
    abstract val to: UserId

    @Serializable
    @SerialName("MESSAGE")
    data class UntaggedMessage(
        override val to: UserId,
        val body: String) : UntaggedEvent()

    @Serializable
    @SerialName("ACK")
    data class UntaggedAck(
        override val to: UserId,
        val body: EventId) : UntaggedEvent()
}
