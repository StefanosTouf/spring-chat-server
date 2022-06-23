package com.steft.chatserver.util.serde.ws_message

import com.steft.chatserver.model.Event
import com.steft.chatserver.util.serde.json.serialize
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Flux

typealias CreateWSMessage = (String) -> WebSocketMessage

fun toWebsocketMessage(createMessage: CreateWSMessage): (Flux<Event>) -> Flux<WebSocketMessage> =
    { events ->
        events
            .transform(::serialize)
            .map { (data) -> createMessage(data) }
    }