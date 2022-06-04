@file:Suppress("NestedLambdaShadowedImplicitParameter")

package com.steft.chatserver.websocket.handle_client.impl

import com.steft.chatserver.messaging.publish_events.PublishEvents
import com.steft.chatserver.model.*
import com.steft.chatserver.websocket.handle_client.FromClient
import com.steft.chatserver.util.serde.deserialize.deserialize
import com.steft.chatserver.util.serde.serialize.serialize
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.OutboundMessage

@Service
class FromClientImpl(private val publishEvents: PublishEvents) : FromClient {
    override fun invoke(userId: UserId): (Flux<Serialized<UntaggedEvent>>) -> Mono<Void> =
        { incoming ->
            val tag = UntaggedEvent.tag(userId)
            incoming
                .map { untagged ->
                    deserialize(untagged)
                        .let(tag)
                        .let { event ->
                            val to = event.to.string
                            val bytes = serialize(event).data.encodeToByteArray()
                            OutboundMessage("", to, bytes)
                        }
                }
                .let(publishEvents)
        }
}
