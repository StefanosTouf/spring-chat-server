package com.steft.chatserver.model

import kotlinx.serialization.Serializable


@Serializable
sealed interface Event

data class Message(
    val messageId: MessageId,
    val from: UserId,
    val to: UserId,
    val body: String) : Event

data class Ack(
    val messageId: MessageId,
    val from: UserId,
    val to: UserId) : Event

@Serializable
sealed interface UntaggedEvent

data class UntaggedMessage(
    val to: UserId,
    val body: String) : UntaggedEvent

data class UntaggedAck(
    val to: UserId,
    val messageId: MessageId) : UntaggedEvent