package com.steft.chatserver.util.tag

import com.steft.chatserver.model.*
import java.util.*

fun UntaggedEvent.Companion.tag(from: UserId): (UntaggedEvent) -> Event =
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