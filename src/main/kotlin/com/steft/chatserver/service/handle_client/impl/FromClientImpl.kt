@file:Suppress("NestedLambdaShadowedImplicitParameter")

package com.steft.chatserver.service.handle_client.impl

import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.model.*
import com.steft.chatserver.service.handle_client.FromClient
import com.steft.chatserver.util.serde.deserialize.deserialize
import com.steft.chatserver.util.serde.serialize.serialize
import com.steft.chatserver.util.tag.tag
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.publish
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.publisher.SynchronousSink
import reactor.rabbitmq.OutboundMessage
import reactor.rabbitmq.Sender
import java.time.Duration
import kotlin.random.Random

@Service
class FromClientImpl(
    private val messagingProperties: MessagingProperties,
    private val sender: Sender) : FromClient {

    override fun invoke(from: UserId): (Flux<Serialized<UntaggedEvent>>) -> Mono<Void> =
        { incoming ->
            val tag = UntaggedEvent.tag(from)
            incoming
                .map { untagged ->
                    deserialize(untagged)
                        .let(tag)
                        .let { event ->
                            val to = event.untagged.to.string
                            val bytes = serialize(event).data.encodeToByteArray()
                            OutboundMessage(
                                messagingProperties.messagingExchangeName,
                                "$to.$from",
                                bytes)
                        }
                }
                .let(sender::send)
        }
}
