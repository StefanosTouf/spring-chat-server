package com.steft.chatserver.service.ack.aknowledge.impl

import com.steft.chatserver.exception.AcknowledgeException
import com.steft.chatserver.model.EventId
import com.steft.chatserver.service.ack.aknowledge.Acknowledge
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AcknowledgeImpl(
    private val zSetOps: ReactiveZSetOperations<String, String>,
    private val reactiveRedisOperations: ReactiveValueOperations<String, String>) : Acknowledge {

    override fun invoke(eventId: EventId): Mono<Void> =
        eventId.id
            .toString()
            .let { idStr ->
                zSetOps
                    .delete(idStr)
                    .then(reactiveRedisOperations
                        .delete(idStr))
            }
            .flatMap { isSuccessful ->
                if (isSuccessful)
                    Mono.empty()
                else
                    Mono.error(
                        AcknowledgeException("Couldn't acknowledge event with id $eventId"))
            }

}