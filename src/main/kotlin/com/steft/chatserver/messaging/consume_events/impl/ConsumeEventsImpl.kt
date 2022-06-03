package com.steft.chatserver.messaging.consume_events.impl

import com.steft.chatserver.messaging.consume_events.ConsumeEvents
import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.rabbitmq.Receiver

@Service
class ConsumeEventsImpl(private val receiver: Receiver) : ConsumeEvents {
    override fun invoke(uid: UserId): Flux<Serialized<Event>> =
        receiver
            .consumeAutoAck(uid.string)
            .map { Serialized(it.body) }
}