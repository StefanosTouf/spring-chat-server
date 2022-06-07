package com.steft.chatserver.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event {
    abstract val eventId: EventId
    abstract val from: UserId
    abstract val to: UserId
}

@Serializable
@SerialName("MESSAGE")
data class Message(
    override val eventId: EventId,
    override val from: UserId,
    override val to: UserId,
    val body: String) : Event()

@Serializable
@SerialName("MESSAGE_ACK")
data class Ack(
    override val eventId: EventId,
    override val from: UserId,
    override val to: UserId,
    val body: EventId) : Event()

@Serializable
@SerialName("IS_ALIVE")
data class IsAlive(
    override val eventId: EventId,
    override val from: UserId,
    override val to: UserId) : Event()

@Serializable
@SerialName("IS_ALIVE_ACK")
data class IsAliveAck(
    override val eventId: EventId,
    override val from: UserId,
    override val to: UserId) : Event()

@Serializable
sealed class UntaggedEvent {
    abstract val to: UserId
}

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

@Serializable
@SerialName("IS_ALIVE")
data class UntaggedIsAlive(override val to: UserId) : UntaggedEvent()

@Serializable
@SerialName("IS_ALIVE_ACK")
data class UntaggedIsAliveAck(override val to: UserId) : UntaggedEvent()
