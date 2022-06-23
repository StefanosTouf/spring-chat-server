package com.steft.chatserver.service.route_events.impl

import com.steft.chatserver.configuration.messaging.MessagingProperties
import com.steft.chatserver.model.Event
import com.steft.chatserver.model.RabbitQueue
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.service.messaging.send_outbound.SendOutbound
import com.steft.chatserver.service.redis.get_queue.GetQueue
import com.steft.chatserver.service.route_events.RouteEvents
import com.steft.chatserver.util.serde.json.serialize
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.OutboundMessage
import java.time.Duration

@Service
class RouteEventsImpl(
    private val messagingProperties: MessagingProperties,
    private val sendOutbound: SendOutbound,
    private val getQueue: GetQueue) : RouteEvents {

    private val log = LoggerFactory.getLogger(this::class.java)

    private fun toOutbound(queue: RabbitQueue): (Serialized<Event>) -> OutboundMessage = { (data) ->
        OutboundMessage(
            messagingProperties.messagingExchangeName,
            queue.string,
            data.encodeToByteArray())
    }

    override fun invoke(incoming: Flux<Event>): Flux<Void> =
        incoming
            .groupBy { it.to }
            .flatMap { events ->
                events
                    .key()
                    .let(getQueue)
                    .flatMapMany { queue ->
                        events
                            .transform(::serialize)
                            .map(toOutbound(queue))
                    }
                    .timeout(Duration.ofMillis(10000), Mono.empty())
                    .doOnComplete { log.info("Timeout group ${events.key()}") }
                    .let(sendOutbound)
            }

}
