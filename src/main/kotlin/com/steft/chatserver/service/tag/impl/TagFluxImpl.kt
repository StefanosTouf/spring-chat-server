package com.steft.chatserver.service.tag.impl

import com.steft.chatserver.model.*
import com.steft.chatserver.service.tag.TagIncomingFlux
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.ByteArrayInputStream
import java.util.UUID


@Service
class TagFluxImpl : TagIncomingFlux {
    @OptIn(ExperimentalSerializationApi::class)
    override fun invoke(from: UserId): (Flux<Serialized<UntaggedEvent>>) -> Flux<Event> = { incoming ->
        incoming
            .flatMap { serialized ->
                ByteArrayInputStream(serialized.data)
                    .let { byteStream ->
                        mono {
                            Json.decodeFromStream<UntaggedEvent>(byteStream)
                        }
                    }
            }.map { untagged ->
                when (untagged) {
                    is UntaggedMessage ->
                        Message(
                            MessageId(UUID.randomUUID()),
                            from,
                            untagged.to,
                            untagged.body)
                    is UntaggedAck ->
                        Ack(
                            MessageId(UUID.randomUUID()),
                            from,
                            untagged.to)
                }
            }
    }

}