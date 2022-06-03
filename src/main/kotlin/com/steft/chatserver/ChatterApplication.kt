@file:Suppress("ReplaceGetOrSet")

package com.steft.chatserver

import com.steft.chatserver.messaging.configuration.MessagingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler

import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriTemplate
import reactor.core.publisher.Flux

import reactor.core.publisher.Mono
import java.time.Duration


@SpringBootApplication
@EnableConfigurationProperties(MessagingProperties::class)
class ChatterApplication

@Configuration
class WebsocketMappings {
    @Bean
    fun webSocketHandlerMapping(handler: WebSocketHandler): HandlerMapping =
        SimpleUrlHandlerMapping(mapOf("/messages/*" to handler), 1)
}

@Component
class ReactiveWebSocketHandler : WebSocketHandler {
    override fun handle(webSocketSession: WebSocketSession): Mono<Void> =
        UriTemplate("/messages/{id}")
            .let { template ->
                Flux.interval(Duration.ofMillis(500))
                    .flatMap { time ->
                        webSocketSession.handshakeInfo.uri.path
                            .let { path -> template.match(path)["id"] }
                            ?.let { id -> "time: $time, hello mister id: $id" }
                            ?.let { Mono.just(it) }
                            ?: Mono.error(Exception("Invalid or nonexistent id parameter"))
                    }
                    .map { payload -> webSocketSession.textMessage(payload) }
                    .let { messages -> webSocketSession.send(messages) }
                    .and(webSocketSession
                        .receive()
                        .map { println("Incoming ${it.payloadAsText}") })
            }
}

fun main(args: Array<String>) {
    runApplication<ChatterApplication>(*args)
}
