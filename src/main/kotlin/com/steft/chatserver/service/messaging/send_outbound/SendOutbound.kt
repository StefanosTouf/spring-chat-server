package com.steft.chatserver.service.messaging.send_outbound

import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.OutboundMessage

interface SendOutbound: (Flux<OutboundMessage>) -> Mono<Void>