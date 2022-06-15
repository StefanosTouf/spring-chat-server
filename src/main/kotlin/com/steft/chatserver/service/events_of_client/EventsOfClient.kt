package com.steft.chatserver.service.events_of_client

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux

interface EventsOfClient: (UserId) -> Flux<Event>