@file:Suppress("ReplaceGetOrSet")

package com.steft.chatserver

import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.model.*
import com.steft.chatserver.util.serde.deserialize.deserialize
import com.steft.chatserver.util.serde.serialize.serialize
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.Receiver
import java.time.Duration

@SpringBootApplication
@EnableConfigurationProperties(MessagingProperties::class)
class ChatterApplication

fun main(args: Array<String>) {
    runApplication<ChatterApplication>(*args)
}
