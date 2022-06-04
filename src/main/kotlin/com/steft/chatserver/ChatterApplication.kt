@file:Suppress("ReplaceGetOrSet")

package com.steft.chatserver

import com.steft.chatserver.messaging.configuration.MessagingProperties
import com.steft.chatserver.model.Serialized
import com.steft.chatserver.model.UntaggedEvent
import com.steft.chatserver.model.UntaggedMessage
import com.steft.chatserver.model.UserId
import com.steft.chatserver.util.serde.deserialize.deserialize
import com.steft.chatserver.util.serde.serialize.serialize
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(MessagingProperties::class)
class ChatterApplication

fun main(args: Array<String>) {
    runApplication<ChatterApplication>(*args)
}