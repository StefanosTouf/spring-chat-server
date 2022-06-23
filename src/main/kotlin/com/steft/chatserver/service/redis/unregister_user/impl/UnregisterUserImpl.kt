package com.steft.chatserver.service.redis.unregister_user.impl

import com.steft.chatserver.exception.UserQueueRegistrationException
import com.steft.chatserver.model.OwnedRabbitQueue
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.redis.register_user.RegisterUser
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UnregisterUserImpl(
    private val redisOps: ReactiveValueOperations<String, String>) : RegisterUser {

    override fun invoke(user: UserId): Mono<Unit> =
        redisOps
            .delete(user.string)
            .flatMap { isSuccessful ->
                if (isSuccessful)
                    Mono.just(Unit)
                else
                    UserQueueRegistrationException("Couldn't register user with id $user")
                        .let { Mono.error(it) }
            }

}