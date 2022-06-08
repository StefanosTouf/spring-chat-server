package com.steft.chatserver.util.tag

import com.steft.chatserver.model.*
import com.steft.chatserver.util.generator.generate

fun UntaggedEvent.Companion.tag(from: UserId): (UntaggedEvent) -> Event =
    { untagged -> Event(EventId.generate(), from, untagged) }