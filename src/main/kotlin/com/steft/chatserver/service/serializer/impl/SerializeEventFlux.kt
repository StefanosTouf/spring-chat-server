package com.steft.chatserver.service.serializer.impl

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.service.serializer.DeserializeEvent
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.ByteArrayInputStream

@Service
class DeserializeEventImpl : DeserializeEvent {
    @OptIn(ExperimentalSerializationApi::class)
    override fun invoke(serializedFlux: Flux<Serialized<Event>>): Flux<Event> =
        serializedFlux.flatMap { serialized ->
            mono {
                ByteArrayInputStream(serialized.data)
                    .let { Json.decodeFromStream(it) }
            }
        }
}

