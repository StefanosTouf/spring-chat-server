package com.steft.chatserver.redis.get_queue.impl

import com.steft.chatserver.model.RabbitQueue
import com.steft.chatserver.model.UserId
import com.steft.chatserver.redis.get_queue.GetQueue
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GetQueueImpl(
    private val redisOps: ReactiveValueOperations<String, String>) : GetQueue {

    override fun invoke(user: UserId): Mono<RabbitQueue> =
        redisOps
            .get(user.string)
            .map { RabbitQueue(it) }

}