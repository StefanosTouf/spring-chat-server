package com.steft.chatserver.service.ack.get_expired_unaknowledged.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.service.ack.get_expired_unaknowledged.GetExpiredUnaknowledged
import com.steft.chatserver.util.serde.deserialize.deserialize
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ReactiveZSetOperations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Date

class GetExpiredUnaknowledgedImpl(
    private val zSetOps: ReactiveZSetOperations<String, String>,
    private val valueOps: ReactiveValueOperations<String, String>) : GetExpiredUnaknowledged {

    private val ackSetKey = "ack" //TODO: Get from config

    private val expiredRange = //TODO: Derive from config
        Range.leftUnbounded(
            Range.Bound.inclusive(Date().time - 1000))

    override fun invoke(): Flux<Event> =
        zSetOps
            .range(ackSetKey, expiredRange)
            .collectList()
            .flatMap(valueOps::multiGet)
            .flatMapIterable { it }
            .flatMap { eventStr ->
                Mono.fromCallable {
                    deserialize<Event>(eventStr)
                }
            }

}