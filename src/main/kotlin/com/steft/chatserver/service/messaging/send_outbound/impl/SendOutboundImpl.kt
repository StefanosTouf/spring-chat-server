package com.steft.chatserver.service.messaging.send_outbound.impl

import com.steft.chatserver.service.messaging.send_outbound.SendOutbound
import org.reactivestreams.Publisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.OutboundMessage
import reactor.rabbitmq.Sender

@Service
class SendOutboundImpl(private val sender: Sender): SendOutbound {
    override fun invoke(messages: Flux<OutboundMessage>): Mono<Void> =
        sender.send(messages)

}