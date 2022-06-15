package com.steft.chatserver.service.route_events.impl

import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.model.Event
import com.steft.chatserver.model.RabbitQueue
import com.steft.chatserver.service.get_queue.GetQueue
import com.steft.chatserver.service.route_events.RouteEvents
import com.steft.chatserver.util.serde.serialize.serialize
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.OutboundMessage
import reactor.rabbitmq.Sender
import java.time.Duration

@Service
class RouteEventsImpl(
    private val messagingProperties: MessagingProperties,
    private val sender: Sender,
    private val getQueue: GetQueue) : RouteEvents {

    private val log = LoggerFactory.getLogger(this::class.java)

    private fun toOutbound(queue: RabbitQueue): (Event) -> OutboundMessage = { event ->
        serialize(event)
            .data
            .encodeToByteArray()
            .let { bytes ->
                OutboundMessage(
                    messagingProperties.messagingExchangeName,
                    queue.string,
                    bytes)
            }
    }

    override fun invoke(incoming: Flux<Event>): Flux<Void> =
        incoming
            .groupBy { it.untagged.to }
            .flatMap { events ->
                events
                    .key()
                    .let(getQueue)
                    .flatMapMany { queue ->
                        events
                            .map(toOutbound(queue))
                            .let(sender::send)
                    }
                    .timeout(Duration.ofMillis(10000), Mono.empty())
                    .doOnComplete { log.info("Timeout group ${events.key()}") }
            }

}
