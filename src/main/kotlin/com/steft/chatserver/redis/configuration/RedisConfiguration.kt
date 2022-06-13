package com.steft.chatserver.redis.configuration

import com.steft.chatserver.model.OwnedRabbitQueue
import com.steft.chatserver.model.RabbitQueue
import com.steft.chatserver.model.UserId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.serializer.RedisElementReader
import org.springframework.data.redis.serializer.RedisElementWriter
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.nio.ByteBuffer
import java.util.UUID

@Configuration
class RedisConfiguration {

    @Bean
    fun ownedRabbitQueue(): OwnedRabbitQueue =
        UUID.randomUUID()
            .toString()
            .let { RabbitQueue(it) }
            .let { OwnedRabbitQueue(it) }

    @Bean
    fun stringOps(connectionFactory: ReactiveRedisConnectionFactory): ReactiveValueOperations<UserId, RabbitQueue> =
        run {
            val userIdSerializer = RedisElementReader<UserId> { buffer ->
                UserId(String(buffer.array()))
            }

            val userIdDeserializer = RedisElementWriter<UserId> { userId ->
                userId.string
                    .toByteArray()
                    .let(ByteBuffer::wrap)
            }

            val queueSerializer = RedisElementReader<RabbitQueue> { buffer ->
                RabbitQueue(String(buffer.array()))
            }

            val queueDeserializer = RedisElementWriter<RabbitQueue> { queue ->
                queue.string
                    .toByteArray()
                    .let(ByteBuffer::wrap)
            }

            RedisSerializationContext
                .newSerializationContext<UserId, RabbitQueue>()
                .key(userIdSerializer, userIdDeserializer)
                .value(queueSerializer, queueDeserializer)
                .build()
                .let { context -> ReactiveRedisTemplate(connectionFactory, context) }
                .opsForValue()

        }

}