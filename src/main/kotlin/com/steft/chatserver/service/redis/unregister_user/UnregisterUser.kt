package com.steft.chatserver.service.redis.unregister_user

import com.steft.chatserver.model.UserId
import reactor.core.publisher.Mono

interface UnregisterUser: (UserId) -> Mono<Unit>