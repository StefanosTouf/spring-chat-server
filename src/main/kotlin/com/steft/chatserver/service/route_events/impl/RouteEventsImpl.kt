package com.steft.chatserver.service.route_events.impl

import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.model.Event
import com.steft.chatserver.model.RabbitQueue
import com.steft.chatserver.model.UserId
import com.steft.chatserver.redis.get_queue.GetQueue
import com.steft.chatserver.service.route_events.RouteEvents
import com.steft.chatserver.util.serde.serialize.serialize
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
                    .doOnComplete { println("Timeout group ${events.key()}") }
            }

}


//fun main() = runBlocking {
//
////    sender.sendWithPublishConfirms(outboundMessage).expand { a ->
////        sender.sendWithPublishConfirms(a.outboundMessage)
////    }
//
//    flux {
//        while (true) {
//            delay(10); send(Random.nextInt(3))
//        }
//    }
//        .groupBy { it }
//        .flatMap { g ->
//            Mono.just(1)
//                .doOnNext { println("First") }
//                .thenMany(g)
//                .flatMap {
////                    sender.sendWithPublishConfirms()
//                    mono { it }
//                }
//                .timeout(Duration.ofMillis(10000), Mono.empty())
//                .doOnComplete { println("Cancelled group ${g.key()}") }
//        }
//        .doOnNext { println(it) }
//        .subscribe()
//
//    delay(1000000)
//}
