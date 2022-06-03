package com.steft.chatserver.messaging.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("messaging")
data class MessagingProperties(
    val host: String,
    val port: Int,
    val password: String,
    val username: String)