package com.steft.chatserver.service.ack.register_unaknowledged

import com.steft.chatserver.model.Event
import reactor.core.publisher.Mono

interface RegisterUnaknowledged: (Event) -> Mono<Void>