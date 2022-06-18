package com.steft.chatserver.service.control_interceptor

import com.steft.chatserver.model.Event
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/*fromClient, toClient*/
interface ControlInterceptor : (Flux<Event.Message>, Flux<Event>) -> Pair<Flux<Event>, Flux<Event.Message>>