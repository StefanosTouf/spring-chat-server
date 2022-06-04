package com.steft.chatserver.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event

@Serializable
@SerialName("MESSAGE")
data class Message(
    val messageId: MessageId,
    val from: UserId,
    val to: UserId,
    val body: String) : Event()

@Serializable
@SerialName("ACK")
data class Ack(
    val messageId: MessageId,
    val from: UserId,
    val to: UserId) : Event()

@Serializable
sealed class UntaggedEvent

@Serializable
@SerialName("MESSAGE")
data class UntaggedMessage(
    val to: UserId,
    val body: String) : UntaggedEvent()

@Serializable
@SerialName("ACK")
data class UntaggedAck(
    val to: UserId,
    val messageId: MessageId) : UntaggedEvent()