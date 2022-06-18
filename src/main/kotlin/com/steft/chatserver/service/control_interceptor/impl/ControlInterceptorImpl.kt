@file:Suppress("NAME_SHADOWING")

package com.steft.chatserver.service.control_interceptor.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.EventId
import com.steft.chatserver.service.control_interceptor.ControlInterceptor
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.Vector

@Service
class ControlInterceptorImpl : ControlInterceptor {

    private val retries = 3

    private fun Flux<Event>.filterMessages(): Flux<Event.Message> =
        flatMap {
            if (it is Event.Message)
                Mono.just(it)
            else
                Mono.empty()
        }

    private fun outputNonAcknowledgedOnInterval(interval: Duration): (Flux<Event>) -> Flux<Event.Message> =
        { messages ->
            List<Map<EventId, Event.Message>>(retries) { HashMap() }
                .let { maps ->
                    messages
                        .buffer(interval)
                        .scan(maps) { maps, events ->
                            val shiftedMaps =
                                maps.drop(1)
                                    .plus(HashMap())

                            events.fold(shiftedMaps) { maps, event ->
                                when (event) {
                                    is Event.Ack ->
                                        maps.map { it.minus(event.eventId) }

                                    is Event.Message ->
                                        maps.mapIndexed { index, map ->
                                            if (index == maps.lastIndex)
                                                map.plus(event.eventId to event)
                                            else
                                                map
                                        }
                                }
                            }
                        }
                        .flatMapIterable { it.asIterable().flatMap { it.asIterable() } }
                        .map { it.value }
                }
        }

    override fun invoke(
        fromClient: Flux<Event.Message>,
        toClient: Flux<Event>): Pair<Flux<Event.Message>, Flux<Event.Message>> =

        toClient
            .filter { it !is Event.Message }
            .mergeWith(fromClient)
            .transform(
                outputNonAcknowledgedOnInterval(
                    Duration.ofMillis(2000)))
            .let { toResend ->
                toClient.filterMessages() to
                        fromClient.mergeWith(toResend)
            }

}

