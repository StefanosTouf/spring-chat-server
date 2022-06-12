@file:Suppress("ReplaceGetOrSet")

package com.steft.chatserver

import com.steft.chatserver.messaging.configuration.MessagingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@EnableConfigurationProperties(MessagingProperties::class)
@SpringBootApplication
class ChatterApplication

fun main(args: Array<String>) {
    runApplication<ChatterApplication>(*args)
}
