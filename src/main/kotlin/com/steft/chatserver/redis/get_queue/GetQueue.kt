package com.steft.chatserver.redis.get_queue

import com.steft.chatserver.model.RabbitQueue
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Mono

interface GetQueue: (UserId) -> Mono<RabbitQueue>