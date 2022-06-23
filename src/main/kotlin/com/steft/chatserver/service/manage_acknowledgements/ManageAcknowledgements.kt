package com.steft.chatserver.service.manage_acknowledgements

import com.steft.chatserver.model.Event
import reactor.core.publisher.Flux

/*fromClient, toClient*/
interface ManageAcknowledgements : (Flux<Event.Message>, Flux<Event>) -> Pair<Flux<Event>, Flux<Event>>