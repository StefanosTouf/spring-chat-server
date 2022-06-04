package com.steft.chatserver.websocket.handle_client

import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UntaggedEvent
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FromClient : (UserId) -> (Flux<Serialized<UntaggedEvent>>) -> Mono<Void>