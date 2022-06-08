package com.steft.chatserver.service.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler

@Configuration
class WebsocketMappings {
    @Bean
    fun webSocketHandlerMapping(handler: WebSocketHandler): HandlerMapping =
        SimpleUrlHandlerMapping(mapOf("/messages/*" to handler), 1)
}