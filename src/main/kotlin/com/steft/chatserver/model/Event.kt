package com.steft.chatserver.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

sealed interface Event {
    val messageId: MessageId
    val from: UserId
    val to: UserId
}

@Serializable
@SerialName("MESSAGE")
data class Message(
    override val messageId: MessageId,
    override val from: UserId,
    override val to: UserId,
    val body: String) : Event

@Serializable
@SerialName("ACK")
data class Ack(
    override val messageId: MessageId,
    override val from: UserId,
    override val to: UserId,
    val body: MessageId) : Event

@Serializable
@SerialName("IS_ALIVE")
data class IsAlive(
    override val messageId: MessageId,
    override val from: UserId,
    override val to: UserId) : Event

sealed interface UntaggedEvent {
    val to: UserId

    companion object {
        fun tag(from: UserId): (UntaggedEvent) -> Event =
            { untagged ->
                when (untagged) {
                    is UntaggedMessage ->
                        Message(
                            MessageId(UUID.randomUUID()),
                            from,
                            untagged.to,
                            untagged.body)
                    is UntaggedAck ->
                        Ack(
                            MessageId(UUID.randomUUID()),
                            from,
                            untagged.to,
                            untagged.body)
                    is UntaggedIsAlive ->
                        IsAlive(
                            MessageId(UUID.randomUUID()),
                            from,
                            untagged.to)
                }
            }
    }
}

@Serializable
@SerialName("MESSAGE")
data class UntaggedMessage(
    override val to: UserId,
    val body: String) : UntaggedEvent

@Serializable
@SerialName("ACK")
data class UntaggedAck(
    override val to: UserId,
    val body: MessageId) : UntaggedEvent

@Serializable
@SerialName("IS_ALIVE")
data class UntaggedIsAlive(override val to: UserId) : UntaggedEvent