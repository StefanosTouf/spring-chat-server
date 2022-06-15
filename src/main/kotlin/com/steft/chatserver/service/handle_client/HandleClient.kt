package com.steft.chatserver.service.handle_client

import com.steft.chatserver.model.*
import com.steft.chatserver.redis.register_user.RegisterUser
import com.steft.chatserver.service.events_of_client.EventsOfClient
import com.steft.chatserver.service.route_events.RouteEvents
import com.steft.chatserver.util.serde.deserialize.deserialize
import com.steft.chatserver.util.serde.serialize.serialize
import com.steft.chatserver.util.tag.tag
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

@Service
class HandleClient(
    private val registerUser: RegisterUser,
    private val routeEvents: RouteEvents,
    private val eventsOfClient: EventsOfClient) : WebSocketHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    private fun getId(session: WebSocketSession): UserId? =
        with(UriTemplate("/messages/{id}")) {
            session.handshakeInfo.uri.path
                .let { match(it)["id"] }
                ?.let { UserId(it) }
        }

    private fun toEvent(userId: UserId): (Flux<WebSocketMessage>) -> Flux<Event> =
        { messages ->
            messages
                .map {
                    it.payload
                        .asByteBuffer()
                        .let(StandardCharsets.UTF_8::decode)
                        .toString()
                }
                .flatMap { message ->
                    Mono.fromCallable {
                        deserialize<UntaggedEvent>(message)
                    }
                }
                .map(UntaggedEvent.tag(userId))
        }

    private fun toWebsocketMessage(session: WebSocketSession): (Flux<Event>) -> Flux<WebSocketMessage> =
        { events ->
            events
                .map { event ->
                    serialize<Event>(event)
                        .data
                        .let(session::textMessage)
                }
        }

    override fun handle(session: WebSocketSession): Mono<Void> =
        getId(session)
            ?.let { userId ->
                registerUser(userId)
                    .then(eventsOfClient(userId)
                        .doOnNext { log.info("Sending $it") }
                        .transform(toWebsocketMessage(session))
                        .let(session::send)
                        .and(session
                            .receive()
                            .transform(toEvent(userId))
                            .doOnNext { log.info("Received $it") }
                            .transform(routeEvents))
                        .doOnError { log.warn(it.toString()) })
            }
            ?: session.close(CloseStatus.POLICY_VIOLATION)

}
