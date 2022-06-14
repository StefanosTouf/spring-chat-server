package com.steft.chatserver.redis.register_user

import com.steft.chatserver.model.UserId
import reactor.core.publisher.Mono

interface RegisterUser: (UserId) -> Mono<Unit>