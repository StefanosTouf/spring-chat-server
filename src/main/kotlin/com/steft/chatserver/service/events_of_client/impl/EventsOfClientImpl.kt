package com.steft.chatserver.service.events_of_client.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.IncomingEvents
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.events_of_client.EventsOfClient
import com.steft.chatserver.util.serde.deserialize.deserialize
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class EventsOfClientImpl(incomingEvents: IncomingEvents) : EventsOfClient {

    private val sharedEvents =
        incomingEvents
            .events
            .flatMap { event ->
                Mono.fromCallable {
                    deserialize(event)
                }
            }
            .share()

    override fun invoke(user: UserId): Flux<Event> =
        sharedEvents
            .filter { it.untagged.to == user }

}