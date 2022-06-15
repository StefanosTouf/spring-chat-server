package com.steft.chatserver.service.ack.get_expired_unaknowledged

import com.steft.chatserver.model.Event
import reactor.core.publisher.Flux

interface GetExpiredUnaknowledged: () -> Flux<Event>