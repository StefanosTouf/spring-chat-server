package com.steft.chatserver.service.await_events.impl

import com.rabbitmq.client.AMQP.Queue.BindOk
import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.service.await_events.AwaitEvents
import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.BindingSpecification
import reactor.rabbitmq.QueueSpecification
import reactor.rabbitmq.Receiver
import reactor.rabbitmq.Sender

private typealias QueueName = String

@Service
class AwaitEventsImpl(
    private val messagingProperties: MessagingProperties,
    private val sender: Sender,
    private val receiver: Receiver) : AwaitEvents {

    private fun declareQueue(): Mono<QueueName> =
        QueueSpecification
            .queue()
            .autoDelete(true)
            .let(sender::declareQueue)
            .map { it.queue }

    private fun bindQueue(user: UserId, queueName: QueueName): Mono<BindOk> =
        BindingSpecification()
            .queue(queueName)
            .exchange(messagingProperties.messagingExchangeName)
            .routingKey("${user.string}.#")
            .let(sender::bind)

    private fun receiveFromQueue(queueName: QueueName): Flux<Serialized<Event>> =
        receiver
            .consumeAutoAck(queueName)
            .map { Serialized(String(it.body)) }

    override fun invoke(user: UserId): Flux<Serialized<Event>> =
        declareQueue()
            .flatMapMany { queueName ->
                bindQueue(user, queueName)
                    .thenMany(receiveFromQueue(queueName))
            }
}