@file:Suppress("ReplaceGetOrSet")

package com.steft.chatserver

import com.steft.chatserver.configuration.messaging.MessagingProperties
import com.steft.chatserver.service.manage_acknowledgements.AcknowledgementProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@EnableConfigurationProperties(
    MessagingProperties::class,
    AcknowledgementProperties::class)
@SpringBootApplication
class ChatterApplication

fun main(args: Array<String>) {
    runApplication<ChatterApplication>(*args)
}
