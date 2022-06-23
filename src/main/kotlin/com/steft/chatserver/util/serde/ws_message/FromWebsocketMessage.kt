package com.steft.chatserver.util.serde.ws_message

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.UntaggedMessage
import com.steft.chatserver.model.UserId
import com.steft.chatserver.util.serde.json.deserialize
import com.steft.chatserver.util.tag.tag
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

fun fromWebsocketMessage(userId: UserId): (Flux<WebSocketMessage>) -> Flux<Event.Message> =
    { messages ->
        messages
            .map {
                it.payload
                    .asByteBuffer()
                    .let(StandardCharsets.UTF_8::decode)
                    .toString()
            }
            .transform { deserialize<UntaggedMessage>(it) }
            .map(UntaggedMessage.tag(userId))
    }

