package com.steft.chatserver.util.serde.ws_message

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.UserId
import com.steft.chatserver.util.serde.json.deserialize
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Flux
import java.nio.charset.StandardCharsets

fun fromWebsocketMessage(messages: Flux<WebSocketMessage>): Flux<Event.Message> =
    messages
        .map {
            it.payload
                .asByteBuffer()
                .let(StandardCharsets.UTF_8::decode)
                .toString()
        }
        .transform { deserialize<Event.Message>(it) }

