package com.steft.chatserver.messaging.publish_events.impl

import com.steft.chatserver.messaging.configuration.MessagingConfiguration
import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.messaging.publish_events.PublishEvents
import com.steft.chatserver.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.BindingSpecification
import reactor.rabbitmq.OutboundMessage
import reactor.rabbitmq.QueueSpecification
import reactor.rabbitmq.Sender

@Service
class PublishEventsImpl(private val sender: Sender) : PublishEvents {
    override fun invoke(outgoing: Flux<OutboundMessage>): Mono<Void> =
        sender.send(outgoing)
}