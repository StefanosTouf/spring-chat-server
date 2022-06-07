package com.steft.chatserver.util.tag

import com.steft.chatserver.model.*
import java.util.*

fun UntaggedEvent.Companion.tag(from: UserId): (UntaggedEvent) -> Event =
    { untagged ->
        when (untagged) {
            is UntaggedMessage ->
                Message(
                    EventId(UUID.randomUUID()),
                    from,
                    untagged.to,
                    untagged.body)
            is UntaggedAck ->
                Ack(
                    EventId(UUID.randomUUID()),
                    from,
                    untagged.to,
                    untagged.body)
            is UntaggedIsAlive ->
                IsAlive(
                    EventId(UUID.randomUUID()),
                    from,
                    untagged.to)
            is UntaggedIsAliveAck ->
                IsAliveAck(
                    EventId(UUID.randomUUID()),
                    from,
                    untagged.to)
        }
    }