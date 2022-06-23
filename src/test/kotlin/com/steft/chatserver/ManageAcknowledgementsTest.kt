package com.steft.chatserver

import com.steft.chatserver.configuration.messaging.MessagingConfiguration
import com.steft.chatserver.integration.Integrator
import com.steft.chatserver.model.Event
import com.steft.chatserver.model.EventId
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.events_of_client.EventsOfClient
import com.steft.chatserver.service.manage_acknowledgements.AcknowledgementProperties
import com.steft.chatserver.service.manage_acknowledgements.ManageAcknowledgements
import com.steft.chatserver.service.manage_acknowledgements.impl.ManageAcknowledgementsImpl
import com.steft.chatserver.service.redis.get_queue.GetQueue
import com.steft.chatserver.service.redis.register_user.RegisterUser
import com.steft.chatserver.service.route_events.RouteEvents
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.UUID

@SpringBootTest
class ManageAcknowledgementsTest {

    @MockBean
    private lateinit var mc: MessagingConfiguration

    @MockBean
    private lateinit var mr: RegisterUser

    @MockBean
    private lateinit var mg: GetQueue

    @MockBean
    private lateinit var mi: Integrator

    @MockBean
    private lateinit var me: EventsOfClient

    @MockBean
    private lateinit var mre: RouteEvents

    @MockBean
    private lateinit var ma: ManageAcknowledgements

    @MockBean
    private lateinit var ap: AcknowledgementProperties

    private val acknowledgementProperties: AcknowledgementProperties =
        AcknowledgementProperties(
            3,
            Duration.ofMillis(2000),
            Duration.ofMillis(200))

    private val manageAcknowledgements: ManageAcknowledgements =
        ManageAcknowledgementsImpl(acknowledgementProperties)
    // Assumes retries are set to >= 2

    private fun createMessageAndAck(sender: UserId, receiver: UserId): (String) -> Pair<Event.Message, Event.Ack> =
        { body ->
            EventId(UUID.randomUUID())
                .let { eventId ->
                    Pair(
                        Event.Message(
                            eventId = eventId,
                            from = sender,
                            to = receiver,
                            body = body),
                        Event.Ack(
                            from = receiver,
                            to = sender,
                            body = eventId))
                }
        }

    @Test
    fun `do incoming acknowledgements result in their target messages not being resent`() {

        val createMessageAndAck = createMessageAndAck(UserId("1"), UserId("2"))

        val (message1, ack1) = createMessageAndAck("1")
        val (message2, ack2) = createMessageAndAck("2")
        val (message3, ack3) = createMessageAndAck("3")

        val fromClientEvents =
            Flux.just(
                message1,
                message2,
                message3)

        val acksToClient = run {
            val halfInterval =
                Mono.delay(
                    acknowledgementProperties
                        .interval
                        .dividedBy(2))

            val interval =
                Mono.delay(acknowledgementProperties.interval)

            Flux.concat<Event>(
                halfInterval.thenReturn(ack1),
                interval.thenReturn(ack2),
                interval.thenReturn(ack3))
        }

        val (fromClient, _) = manageAcknowledgements(fromClientEvents, acksToClient)

        StepVerifier
            .create(fromClient)
            .expectNext(message1)
            .expectNext(message2)
            .expectNext(message3)
            .thenAwait(acknowledgementProperties.interval)
            .expectNext(message2)
            .expectNext(message3)
            .thenAwait(acknowledgementProperties.interval)
            .expectNext(message3)
            .expectTimeout(
                acknowledgementProperties.interval
                    .multipliedBy(3))
            .verify()
    }

    @Test
    fun `are acks being emitted as responses to messages`() {
        val createMessageAndAck = createMessageAndAck(UserId("1"), UserId("2"))

        fun isAckAndEqualsOtherIgnoringId(ack1: Event, ack2: Event.Ack) =
            when (ack1) {
                is Event.Ack ->
                    ack1.let { (_, to1, from1, body1) ->
                        ack2.let { (_, to2, from2, body2) ->
                            to1 == to2 && from1 == from2 && body1 == body2
                        }
                    }
                is Event.Message ->
                    false
            }

        val (message1, ack1) = createMessageAndAck("1")
        val (message2, ack2) = createMessageAndAck("2")
        val (message3, ack3) = createMessageAndAck("3")

        val toClient =
            Flux.just<Event>(
                message1,
                message2,
                message3)

        val fromClient =
            Flux.empty<Event.Message>()

        val (fromClientAcks, _) = manageAcknowledgements(fromClient, toClient)

        StepVerifier
            .create(fromClientAcks)
            .expectNextMatches { t -> isAckAndEqualsOtherIgnoringId(t, ack1) }
            .expectNextMatches { t -> isAckAndEqualsOtherIgnoringId(t, ack2) }
            .expectNextMatches { t -> isAckAndEqualsOtherIgnoringId(t, ack3) }
            .thenCancel()
            .verify()
    }

    @Test
    fun `are events resent appropriately`() {

        val sender = UserId("1")
        val receiver = UserId("2")

        fun createMessage(body: String) =
            Event.Message(
                from = sender,
                to = receiver,
                body = body)

        val message1 = createMessage("1")
        val message2 = createMessage("2")
        val message3 = createMessage("3")

        val toClient =
            Flux.empty<Event>()

        val fromClient =
            Flux.just(
                message1,
                message2,
                message3)

        val (fromClientWithResent, _) = manageAcknowledgements(fromClient, toClient)

        (1..acknowledgementProperties.retries)
            .fold(
                StepVerifier
                    .create(
                        fromClientWithResent
                            .doOnNext { println(it) })
                    .expectSubscription()
                    .expectNext(message1)
                    .expectNext(message2)
                    .expectNext(message3))
            { acc, _ ->
                acc.thenAwait(acknowledgementProperties.interval)
                    .expectNext(message1)
                    .expectNext(message2)
                    .expectNext(message3)
            }
            .expectTimeout(
                acknowledgementProperties.interval
                    .multipliedBy(2))
            .verify()
    }
}