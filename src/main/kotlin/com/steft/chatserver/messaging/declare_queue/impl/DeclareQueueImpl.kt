package com.steft.chatserver.messaging.declare_queue.impl

import com.rabbitmq.client.AMQP.Queue.DeclareOk
import com.steft.chatserver.model.UserId
import com.steft.chatserver.messaging.declare_queue.DeclareQueue
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.rabbitmq.QueueSpecification
import reactor.rabbitmq.Sender

@Service
class DeclareQueueImpl(private val sender: Sender) : DeclareQueue {
    override fun invoke(userId: UserId): Mono<DeclareOk> =
        QueueSpecification
            .queue()
            .name(userId.string)
            .autoDelete(true)
            .let(sender::declareQueue)
}