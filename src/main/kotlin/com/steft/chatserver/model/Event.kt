package com.steft.chatserver.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event {
    abstract val messageId: MessageId
    abstract val from: UserId
    abstract val to: UserId
}

@Serializable
@SerialName("MESSAGE")
data class Message(
    override val messageId: MessageId,
    override val from: UserId,
    override val to: UserId,
    val body: String) : Event()

@Serializable
@SerialName("ACK")
data class Ack(
    override val messageId: MessageId,
    override val from: UserId,
    override val to: UserId,
    val body: MessageId) : Event()

@Serializable
@SerialName("IS_ALIVE")
data class IsAlive(
    override val messageId: MessageId,
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
    val body: MessageId) : UntaggedEvent()

@Serializable
@SerialName("IS_ALIVE")
data class UntaggedIsAlive(override val to: UserId) : UntaggedEvent()