package com.steft.chatserver.service.declare_connection.impl

import com.rabbitmq.client.Channel
import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.declare_connection.AwaitEvents
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.rabbitmq.QueueSpecification
import reactor.rabbitmq.Receiver
import reactor.rabbitmq.Sender

@Service
class AwaitEventsImpl(
    private val sender: Sender,
    private val receiver: Receiver) : AwaitEvents {

    override fun invoke(uid: UserId): Flux<Serialized<Event>> =
        QueueSpecification
            .queue()
            .autoDelete(true)
            .let(sender::declareQueue)
            .thenMany(
                receiver
                    .consumeAutoAck(uid.string)
                    .map { Serialized(it.body) })
}