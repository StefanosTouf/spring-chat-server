package com.steft.chatserver.model

import reactor.core.publisher.Flux

@JvmInline
value class IncomingEvents(val events: Flux<Event>)
