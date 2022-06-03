package com.steft.chatserver.service.handle_client

import com.steft.chatserver.messaging.declare_queue.DeclareQueue
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UntaggedEvent
import com.steft.chatserver.model.UserId
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriTemplate
import reactor.core.publisher.Mono

@Service
class HandleClient(
    private val declareQueue: DeclareQueue,
    private val fromClient: FromClient,
    private val toClient: ToClient) : WebSocketHandler {

    private fun getId(session: WebSocketSession): UserId? =
        with(UriTemplate("/messages/{id}")) {
            session.handshakeInfo.uri.path
                .let { match(it)["id"] }
                ?.let { UserId(it) }
        }

    override fun handle(session: WebSocketSession): Mono<Void> =
        getId(session)
            ?.let { userId ->
                declareQueue(userId)
                    .then(
                        toClient(userId)
                            .map { session.textMessage(String(it.data)) }//TODO: ByteArray -> String is messy, fix it
                            .let { session.send(it) }
                            .and(
                                session
                                    .receive()
                                    .map { message ->
                                        Serialized<UntaggedEvent>(
                                            message.payload
                                                .asByteBuffer()
                                                .array()) //TODO: DataBuffer -> ByteArray is messy, fix it
                                    }.transform(fromClient(userId))))
            }
            ?: Mono.error(Exception("Invalid or nonexistent id parameter")) //TODO: Better error handling

}