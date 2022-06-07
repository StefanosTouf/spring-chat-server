package com.steft.chatserver.messaging.declare_queue

import com.rabbitmq.client.AMQP.Queue.BindOk
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Mono

interface DeclareQueue : (UserId) -> Mono<BindOk>