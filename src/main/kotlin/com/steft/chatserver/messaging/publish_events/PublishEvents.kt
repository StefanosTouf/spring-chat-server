package com.steft.chatserver.messaging.publish_events

import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.OutboundMessage

interface PublishEvents: (Flux<OutboundMessage>) -> Mono<Void>