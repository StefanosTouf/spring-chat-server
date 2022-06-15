package com.steft.chatserver.service.register_user

import com.steft.chatserver.model.UserId
import reactor.core.publisher.Mono

interface RegisterUser: (UserId) -> Mono<Unit>