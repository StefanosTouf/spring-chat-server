package com.steft.chatserver.integration

import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.manage_acknowledgements.ManageAcknowledgements
import com.steft.chatserver.service.events_of_client.EventsOfClient
import com.steft.chatserver.service.redis.register_user.RegisterUser
import com.steft.chatserver.service.route_events.RouteEvents
import com.steft.chatserver.util.serde.ws_message.CreateWSMessage
import com.steft.chatserver.util.serde.ws_message.fromWebsocketMessage
import com.steft.chatserver.util.serde.ws_message.toWebsocketMessage
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

typealias FromClient = Flux<WebSocketMessage>
typealias CreateToClient = (Flux<WebSocketMessage>) -> Mono<Void>
typealias Integration = (UserId, CreateWSMessage) -> (FromClient, CreateToClient) -> Mono<Void>

@Service
class Integrator(
    private val manageAcknowledgements: ManageAcknowledgements,
    private val registerUser: RegisterUser,
    private val routeEvents: RouteEvents,
    private val eventsOfClient: EventsOfClient) : Integration {

    override fun invoke(user: UserId, createMessage: CreateWSMessage): (FromClient, CreateToClient) -> Mono<Void> =
        { fromClient, createToClient ->
            fromClient
                .transform(fromWebsocketMessage(user))
                .let { eventsFromClient -> manageAcknowledgements(eventsFromClient, eventsOfClient(user)) }
                .let { (fromClient, toClient) ->
                    registerUser(user)
                        .then(
                            toClient
                                .transform(toWebsocketMessage(createMessage))
                                .let(createToClient)
                                .and(fromClient.transform(routeEvents)))
                }
        }

}