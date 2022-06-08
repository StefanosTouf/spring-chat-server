package com.steft.chatserver.util.generator

import com.steft.chatserver.model.EventId
import java.util.UUID

fun EventId.Companion.generate() = EventId(UUID.randomUUID())