package com.steft.chatserver.messaging.configuration

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.steft.chatserver.model.*
import com.steft.chatserver.util.serde.deserialize.deserialize
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.rabbitmq.*
import java.util.*

@Configuration
class MessagingConfiguration {

    @Bean
    fun connectionFactory(configuration: MessagingProperties): Mono<Connection> =
        ConnectionFactory()
            .apply {
                useNio()
                host = configuration.host
                port = configuration.port
                password = configuration.password
                username = configuration.username
            }.let { connectionFactory ->
                Mono.fromCallable {
                    connectionFactory
                        .newConnection("reactor-rabbit")
                }
            }.cache()

    @Bean
    fun receiver(
        ownedRabbitQueue: OwnedRabbitQueue,
        connection: Mono<Connection>): IncomingEvents =
        ReceiverOptions()
            .connectionMono(connection)
            .let(RabbitFlux::createReceiver)
            .consumeAutoAck(ownedRabbitQueue.rabbitQueue.string)
            .map { deserialize<Event>(String(it.body)) }
            .share()
            .let { IncomingEvents(it) }

    @Bean
    fun sender(connection: Mono<Connection>): Sender =
        SenderOptions()
            .connectionMono(connection)
            .let(RabbitFlux::createSender)

    @Bean
    fun createMessagingExchange(configuration: MessagingProperties, sender: Sender): Disposable =
        sender.declareExchange(
            ExchangeSpecification()
                .name(configuration.messagingExchangeName)
                .type("topic"))
            .subscribe()

    @Bean
    fun ownedRabbitQueue(): OwnedRabbitQueue =
        UUID.randomUUID()
            .toString()
            .let { RabbitQueue(it) }
            .let { OwnedRabbitQueue(it) }

}