package com.steft.chatserver.service.declare_connection

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux

interface AwaitEvents: (UserId) -> Flux<Serialized<Event>>