@file:Suppress("ReplaceGetOrSet")

package com.steft.chatserver

import com.steft.chatserver.messaging.configuration.MessagingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(MessagingProperties::class)
class ChatterApplication

fun main(args: Array<String>) {
    runApplication<ChatterApplication>(*args)
}

//fun main() = runBlocking {
//    UntaggedMessage(UserId("1"), "asdasdasd")
//        .let { serialize<UntaggedEvent>(it) }
//        .let { println(it) }
//
//}