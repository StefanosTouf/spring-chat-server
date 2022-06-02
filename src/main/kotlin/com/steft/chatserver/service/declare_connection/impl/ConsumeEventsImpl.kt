package com.steft.chatserver.service.declare_connection.impl

import com.steft.chatserver.service.declare_connection.ConsumeEvents
import com.steft.chatserver.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.rabbitmq.Receiver

@Service
class ConsumeEventsImpl(private val receiver: Receiver): ConsumeEvents {
    override fun invoke(uid: UserId): Flux<ByteArray> =
        receiver
            .consumeAutoAck(uid.string)
            .map { it.body }
}