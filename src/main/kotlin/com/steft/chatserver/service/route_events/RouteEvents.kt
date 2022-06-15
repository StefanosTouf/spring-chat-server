package com.steft.chatserver.service.route_events

import com.steft.chatserver.model.Event
import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux

interface RouteEvents : (Flux<Event>) -> Flux<Void>
