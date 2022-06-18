package com.steft.chatserver.util.tag

import com.steft.chatserver.model.*
import com.steft.chatserver.util.generator.generate

fun UntaggedMessage.Companion.tag(from: UserId): (UntaggedMessage) -> Event.Message =
    { untagged -> Event.Message(EventId.generate(), untagged.to, from, untagged.body) }