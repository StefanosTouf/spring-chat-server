package com.steft.chatserver.util.event

import com.steft.chatserver.model.Event
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.stream.Collector

/**
 * Splits a list of Events into a list of Acks and a list of Messages.
 * Uses mutable lists to optimise this procedure
 */
fun List<Event>.split(): Pair<List<Event.Ack>, List<Event.Message>> =
    (mutableListOf<Event.Ack>() to mutableListOf<Event.Message>())
        .also { (acks, messages) ->
            this.forEach { event ->
                when (event) {
                    is Event.Ack ->
                        acks.add(event)
                    is Event.Message ->
                        messages.add(event)
                }
            }
        }


