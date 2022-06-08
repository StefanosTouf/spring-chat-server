package com.steft.chatserver.service.handle_client.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.await_events.AwaitEvents
import com.steft.chatserver.service.handle_client.ToClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ToClientImpl(private val awaitEvents: AwaitEvents): ToClient {
    override fun invoke(user: UserId): Flux<Serialized<Event>> =
        awaitEvents(user)
}