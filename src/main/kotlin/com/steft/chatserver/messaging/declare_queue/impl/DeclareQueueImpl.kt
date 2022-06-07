package com.steft.chatserver.messaging.declare_queue.impl

import com.rabbitmq.client.AMQP.Queue.BindOk
import com.rabbitmq.client.AMQP.Queue.DeclareOk
import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.model.UserId
import com.steft.chatserver.messaging.declare_queue.DeclareQueue
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.rabbitmq.BindingSpecification
import reactor.rabbitmq.QueueSpecification
import reactor.rabbitmq.Sender

@Service
class DeclareQueueImpl(
    private val messagingProperties: MessagingProperties,
    private val sender: Sender) : DeclareQueue {

    override fun invoke(user: UserId): Mono<BindOk> =
        QueueSpecification
            .queue(user.string)
            .autoDelete(true)
            .let(sender::declareQueue)
            .then(
                sender.bind(
                    BindingSpecification()
                        .queue(user.string)
                        .exchange(messagingProperties.messagingExchangeName)
                        .routingKey("${user.string}.#")))
}