package com.steft.chatserver.service.handle_client

import com.steft.chatserver.model.IncomingEvents
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UntaggedEvent
import com.steft.chatserver.model.UserId
import com.steft.chatserver.util.serde.serialize.serialize
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriTemplate
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

@Service
class HandleClient(
    private val fromClient: FromClient,
    private val incomingEvents: IncomingEvents) : WebSocketHandler {

    private fun getId(session: WebSocketSession): UserId? =
        with(UriTemplate("/messages/{id}")) {
            session.handshakeInfo.uri.path
                .let { match(it)["id"] }
                ?.let { UserId(it) }
        }

    private fun messageToSerialized(user: UserId, message: WebSocketMessage): Serialized<UntaggedEvent> =
        message.payload
            .asByteBuffer()
            .let(StandardCharsets.UTF_8::decode)
            .also { println("Incoming message from $user: $it") }
            .let { Serialized(it.toString()) }

    override fun handle(session: WebSocketSession): Mono<Void> =
        getId(session)
            ?.let { userId ->
                incomingEvents
                    .events
                    .filter { it.untagged.to == userId }
                    .map { event ->
                        serialize(event)
                            .data
                            .let(session::textMessage)
                    }
                    .let(session::send)
                    .and(session
                        .receive()
                        .map { messageToSerialized(userId, it) }
                        .transform(fromClient(userId)))
                    .doOnError { println("Error: $it") }
            }
            ?: session.close(CloseStatus.POLICY_VIOLATION)

}