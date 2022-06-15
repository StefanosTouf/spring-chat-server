package com.steft.chatserver.service.ack.register_unaknowledged.impl

import com.steft.chatserver.exception.RegisterUnacknowledgedException
import com.steft.chatserver.model.Event
import com.steft.chatserver.service.ack.register_unaknowledged.RegisterUnaknowledged
import com.steft.chatserver.util.serde.serialize.serialize
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.Date

@Service
class RegisterUnacknowledgedImpl(
    private val zSetOps: ReactiveZSetOperations<String, String>,
    private val valueOps: ReactiveValueOperations<String, String>) : RegisterUnaknowledged {

    private val ackSetKey = "ack" //TODO: Get from config

    override fun invoke(event: Event): Mono<Void> =
        Mono.fromCallable { serialize(event) }
            .map { (serializedEvent) ->
                Triple(
                    serializedEvent,
                    event.eventId.id.toString(),
                    Date().time.toDouble())
            }
            .flatMap { (event, id, timestamp) ->
                zSetOps
                    .add(ackSetKey, id, timestamp)
                    .then(valueOps.set(id, event))
            }
            .flatMap { isSuccessful ->
                if (isSuccessful)
                    Mono.empty()
                else
                    Mono.error(
                        RegisterUnacknowledgedException(
                            "Couldn't register $event as unacknowledged"))
            }


}