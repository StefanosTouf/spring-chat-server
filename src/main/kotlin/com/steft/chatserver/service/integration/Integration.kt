package com.steft.chatserver.service.integration

import com.steft.chatserver.model.UserId
import com.steft.chatserver.util.serde.ws_message.CreateWSMessage
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

typealias FromClient = Flux<WebSocketMessage>
typealias CreateToClient = (Flux<WebSocketMessage>) -> Mono<Void>

interface Integration: (UserId, CreateWSMessage) -> (FromClient, CreateToClient) -> Mono<Void>