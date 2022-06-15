package com.steft.chatserver.model

import reactor.core.publisher.Flux

data class IncomingEvents(val events: Flux<Serialized<Event>>)
