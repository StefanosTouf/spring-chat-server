package com.steft.chatserver.websocket

import com.steft.chatserver.integration.Integration
import com.steft.chatserver.model.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriTemplate
import reactor.core.publisher.Mono

@Service
class HandleClient(
    private val integration: Integration) : WebSocketHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    private fun getId(session: WebSocketSession): UserId? =
        with(UriTemplate("/messages/{id}")) {
            session.handshakeInfo.uri.path
                .let { match(it)["id"] }
                ?.let { UserId(it) }
        }

    override fun handle(session: WebSocketSession): Mono<Void> =
        getId(session)
            ?.also { log.info("Connection from user $it") }
            ?.let { userId ->
                integration(
                    userId,
                    session::textMessage)(
                    session.receive(),
                    session::send)
            }
            ?: session.close(CloseStatus.POLICY_VIOLATION)

}
