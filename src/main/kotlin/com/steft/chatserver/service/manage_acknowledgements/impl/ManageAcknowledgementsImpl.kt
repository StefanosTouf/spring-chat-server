@file:Suppress("NAME_SHADOWING")

package com.steft.chatserver.service.manage_acknowledgements.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.EventId
import com.steft.chatserver.service.manage_acknowledgements.AcknowledgementProperties
import com.steft.chatserver.service.manage_acknowledgements.ManageAcknowledgements
import com.steft.chatserver.util.event.split
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.Duration

private typealias UnaknowledgedMessages = List<Map<EventId, Event.Message>>

@Service
class ManageAcknowledgementsImpl(
    private val acknowledgementProperties: AcknowledgementProperties) : ManageAcknowledgements {

    private val failHandler =
        Sinks.EmitFailureHandler
            .busyLooping(acknowledgementProperties.nonSerializedBusyLooping)

    private fun <T> makeSink(): Sinks.Many<T> =
        Sinks.many()
            .unicast()
            .onBackpressureBuffer()

    private fun splitMessagesAndAcks(events: Flux<Event>): Pair<Flux<Event.Message>, Flux<Event.Ack>> =
        makeSink<Event.Ack>()
            .let { ackSink ->
                events.flatMap { event ->
                    when (event) {
                        is Event.Message ->
                            Mono.just(event)
                        is Event.Ack ->
                            Mono.fromCallable {
                                ackSink.emitNext(event, failHandler)
                            }.then(Mono.empty())
                    }
                }.let { it to ackSink.asFlux() }
            }

    private fun acknowledgeMessages(events: Flux<Event.Message>): Flux<Event.Ack> =
        events.map { message ->
            Event.Ack(
                from = message.to,
                to = message.from,
                body = message.eventId
            )
        }

    /*
        Uses window + collectList instead of buffer to produce empty lists when window is empty.
        buffer doesn't output empty lists when no elements have arrived within the interval.
        Empty lists outputted are necessary to still output non acknowledged messages (and
        execute the rest of the acknowledgement logic) when no new messages are being received.
     */
    private fun outputNonAcknowledgedOnInterval(
        outgoingMessages: Flux<Event.Message>,
        incomingAcks: Flux<Event.Ack>): Flux<Event.Message> = run {

        val unaknowledgedMessages: UnaknowledgedMessages =
            List<Map<EventId, Event.Message>>(acknowledgementProperties.retries)
            { HashMap() }

        Flux.merge(outgoingMessages, incomingAcks)
            .window(acknowledgementProperties.interval)
            .flatMap { it.collectList() }
            .scan(unaknowledgedMessages) { unaknowledged, events ->
                events
                    .split()
                    .let { (acks, messages) ->
                        messages
                            .associateBy { it.eventId }
                            .let { newMessages -> unaknowledged.plus(newMessages) }
                            .drop(1)
                            .let { newUnaknowledged ->
                                acks.fold(newUnaknowledged) { acc, event ->
                                    acc.map { it.minus(event.body) }
                                }
                            }
                    }
            }
            .flatMapIterable { unaknowledged ->
                unaknowledged.flatMap { it.values }
            }
    }

    private fun <T> duplicate(flux: Flux<T>) =
        flux.publish()
            .refCount(2)
            .let { it to it }

    override fun invoke(
        fromClient: Flux<Event.Message>,
        toClient: Flux<Event>): Pair<Flux<Event>, Flux<Event>> = run {

        val (controlMessagesFromClient, messagesFromClient) =
            duplicate(fromClient)

        val (controlEventsToClient, eventsToClient) =
            duplicate(toClient)

        val (controlMessagesToClient, controlAcksToClient) =
            splitMessagesAndAcks(controlEventsToClient)

        val nonAcknowledgedFromClient =
            outputNonAcknowledgedOnInterval(
                controlMessagesFromClient,
                controlAcksToClient)

        val acksFromClient =
            controlMessagesToClient
                .transform(::acknowledgeMessages)

        val eventsFromClient =
            Flux.merge(
                messagesFromClient,
                nonAcknowledgedFromClient,
                acksFromClient)

        eventsFromClient to eventsToClient

    }
}










