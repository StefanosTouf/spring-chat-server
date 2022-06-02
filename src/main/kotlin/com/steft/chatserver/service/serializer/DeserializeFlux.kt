package com.steft.chatserver.service.serializer

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import reactor.core.publisher.Flux

interface DeserializeEvent : (Flux<Serialized<Event>>) -> Flux<Event>