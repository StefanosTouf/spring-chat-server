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
    fun sender(connection: Mono<Connection>): Sender =
        SenderOptions()
            .connectionMono(connection)
            .let(RabbitFlux::createSender)

    @Bean
    fun createMessagingExchange(
        configuration: MessagingProperties,
        sender: Sender): Disposable =
        sender
            .declareExchange(
                ExchangeSpecification()
                    .name(configuration.messagingExchangeName)
                    .type("direct"))
            .subscribe()

    @Bean
    fun receiver(
        configuration: MessagingProperties,
        sender: Sender,
        ownedRabbitQueue: OwnedRabbitQueue,
        connection: Mono<Connection>): IncomingEvents =
        QueueSpecification()
            .exclusive(true)
            .let(sender::declareQueue)
            .flatMap { declareOk ->
                BindingSpecification()
                    .queue(declareOk.queue)
                    .routingKey(ownedRabbitQueue.rabbitQueue.string)
                    .exchange(configuration.messagingExchangeName)
                    .let(sender::bind)
                    .thenReturn(declareOk)
            }
            .flatMapMany { declareOk ->
                ReceiverOptions()
                    .connectionMono(connection)
                    .let(RabbitFlux::createReceiver)
                    .consumeAutoAck(declareOk.queue)
                    .map { Serialized<Event>(String(it.body)) }

            }.let { IncomingEvents(it) }


    @Bean
    fun ownedRabbitQueue(): OwnedRabbitQueue =
        UUID.randomUUID()
            .toString()
            .let { RabbitQueue(it) }
            .let { OwnedRabbitQueue(it) }

}