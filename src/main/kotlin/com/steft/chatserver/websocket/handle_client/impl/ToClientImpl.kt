package com.steft.chatserver.websocket.handle_client.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import com.steft.chatserver.messaging.consume_events.ConsumeEvents
import com.steft.chatserver.websocket.handle_client.ToClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ToClientImpl(private val consumeEvents: ConsumeEvents): ToClient {
    override fun invoke(user: UserId): Flux<Serialized<Event>> =
        consumeEvents(user)
}