package com.steft.chatserver.messaging.consume_events

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux

interface ConsumeEvents: (UserId) -> Flux<Serialized<Event>>