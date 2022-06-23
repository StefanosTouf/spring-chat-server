package com.steft.chatserver.service.events_of_client.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.IncomingEvents
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.events_of_client.EventsOfClient
import com.steft.chatserver.util.serde.json.deserialize
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class EventsOfClientImpl(incomingEvents: IncomingEvents) : EventsOfClient {

    private val sharedEvents =
        incomingEvents
            .events
            .transform(::deserialize)
            .share()

    override fun invoke(user: UserId): Flux<Event> =
        sharedEvents
            .filter { it.to == user }

}