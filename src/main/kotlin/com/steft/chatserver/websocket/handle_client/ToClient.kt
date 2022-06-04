package com.steft.chatserver.websocket.handle_client

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux

interface ToClient : (UserId) -> Flux<Serialized<Event>>