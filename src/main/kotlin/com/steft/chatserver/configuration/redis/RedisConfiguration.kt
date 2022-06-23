package com.steft.chatserver.configuration.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ReactiveZSetOperations

@Configuration
class RedisConfiguration {

    @Bean
    fun stringOps(reactiveStringRedisTemplate: ReactiveStringRedisTemplate): ReactiveValueOperations<String, String> =
        reactiveStringRedisTemplate.opsForValue()

    @Bean
    fun sortedSetOps(reactiveStringRedisTemplate: ReactiveStringRedisTemplate): ReactiveZSetOperations<String, String> =
        reactiveStringRedisTemplate.opsForZSet()

}