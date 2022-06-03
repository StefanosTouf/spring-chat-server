package com.steft.chatserver.messaging.configuration

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.rabbitmq.*

@Configuration
class MessagingConfiguration {

    @Bean
    fun connectionFactory(): Mono<Connection> =
        ConnectionFactory()
            .apply {
                useNio()
                host = "localhost"
                port = 5672
                password = "guest"
                username = "guest"
            }.let { connectionFactory ->
                mono {
                    connectionFactory
                        .newConnection("reactor-rabbit")
                }
            }.cache()

    @Bean
    fun receiver(connection: Mono<Connection>): Receiver =
        ReceiverOptions()
            .connectionMono(connection)
            .let(RabbitFlux::createReceiver)

    @Bean
    fun sender(connection: Mono<Connection>): Sender =
        SenderOptions()
            .connectionMono(connection)
            .let(RabbitFlux::createSender)
}