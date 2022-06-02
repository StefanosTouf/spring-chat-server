package com.steft.chatserver.service.declare_connection

import com.steft.chatserver.model.UserId
import reactor.core.publisher.Flux

interface ConsumeEvents: (UserId) -> Flux<ByteArray>