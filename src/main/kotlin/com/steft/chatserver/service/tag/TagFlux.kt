package com.steft.chatserver.service.tag

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UntaggedEvent
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux

interface TagIncomingFlux: (UserId) -> (Flux<Serialized<UntaggedEvent>>) -> Flux<Event>