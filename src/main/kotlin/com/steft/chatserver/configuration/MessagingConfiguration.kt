package com.steft.chatserver.configuration

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.rabbitmq.RabbitFlux
import reactor.rabbitmq.Receiver
import reactor.rabbitmq.ReceiverOptions
import reactor.rabbitmq.Sender
import reactor.rabbitmq.SenderOptions


@Configuration
class MessagingConfiguration {

    @Bean("rabbit-connection")
    fun connectionFactory(): Mono<Connection> =
        ConnectionFactory()
            .apply {
                useNio()
                host = "localhost"
                port = 5672
                password = "guest"
                username = "guest"
            }.let { connectionFactory ->
                Mono.fromCallable {
                    connectionFactory
                        .newConnection("reactor-rabbit")
                }
            }.cache()

    @Bean
    fun receiver(connection: Mono<Connection>): Receiver =
        ReceiverOptions()
            .connectionMono(connection)
            .let(RabbitFlux::createReceiver)

    @Bean("rabbit-sender")
    fun sender(connection: Mono<Connection?>?): Sender =
        SenderOptions()
            .connectionMono(connection)
            .let(RabbitFlux::createSender)
}