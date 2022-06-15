package com.steft.chatserver.redis.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations

@Configuration
class RedisConfiguration {

    @Bean
    fun stringOps(reactiveStringRedisTemplate: ReactiveStringRedisTemplate): ReactiveValueOperations<String, String> =
        reactiveStringRedisTemplate.opsForValue()

}