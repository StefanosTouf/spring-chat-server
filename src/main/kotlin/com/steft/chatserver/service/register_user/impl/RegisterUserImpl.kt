package com.steft.chatserver.service.register_user.impl

import com.steft.chatserver.exception.UserQueueRegistrationException
import com.steft.chatserver.model.OwnedRabbitQueue
import com.steft.chatserver.model.UserId
import com.steft.chatserver.service.register_user.RegisterUser
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterUserImpl(
    private val redisOps: ReactiveValueOperations<String, String>,
    private val ownedRabbitQueue: OwnedRabbitQueue) : RegisterUser {

    override fun invoke(user: UserId): Mono<Unit> =
        redisOps
            .set(user.string, ownedRabbitQueue.rabbitQueue.string)
            .flatMap { isSuccessful ->
                if (isSuccessful)
                    Mono.just(Unit)
                else
                    UserQueueRegistrationException("Couldn't register user with id $user")
                        .let { Mono.error(it) }
            }

}