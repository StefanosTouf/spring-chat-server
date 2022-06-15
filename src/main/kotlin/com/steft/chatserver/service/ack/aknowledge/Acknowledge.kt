package com.steft.chatserver.service.ack.aknowledge

import com.steft.chatserver.model.EventId
import reactor.core.publisher.Mono

interface Acknowledge: (EventId) -> Mono<Void>