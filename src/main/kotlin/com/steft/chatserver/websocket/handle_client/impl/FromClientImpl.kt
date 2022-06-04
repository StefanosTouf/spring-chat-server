@file:Suppress("NestedLambdaShadowedImplicitParameter")

package com.steft.chatserver.service.handle_client.impl

import com.steft.chatserver.messaging.publish_events.PublishEvents
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UntaggedEvent
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.handle_client.FromClient
import com.steft.chatserver.util.serde.deserialize.deserialize
import com.steft.chatserver.util.serde.serialize.serialize
import com.steft.chatserver.util.tag.tagEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.OutboundMessage

@Service
class FromClientImpl(private val publishEvents: PublishEvents) : FromClient {
    override fun invoke(userId: UserId): (Flux<Serialized<UntaggedEvent>>) -> Mono<Void> =
        { incoming ->
            val tagEvent = tagEvent(userId)
            incoming
                .map { untagged ->
                    deserialize(untagged)
                        .let(tagEvent)
                        .let { serialize(it) }
                        .let { (data) ->
                            OutboundMessage("", userId.string, data)
                        }
                }
                .let { publishEvents(it) }
        }
}