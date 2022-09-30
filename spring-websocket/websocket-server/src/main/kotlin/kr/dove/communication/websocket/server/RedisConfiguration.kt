package kr.dove.communication.websocket.server

import com.fasterxml.jackson.databind.ObjectMapper
import kr.dove.communication.api.Message
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfiguration {

    @Primary
    @Bean
    fun lettuceConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = LettuceClientConfiguration
            .builder()
            .commandTimeout(Duration.ofSeconds(1L))
            .shutdownTimeout(Duration.ZERO)
            .build()

        return LettuceConnectionFactory(
            RedisStandaloneConfiguration("localhost", 6379),
            config
        )
    }

    @Bean
    fun messageRedisTemplate(
        lettuceConnectionFactory: ReactiveRedisConnectionFactory,
        objectMapper: ObjectMapper,
    ) : ReactiveRedisTemplate<String, Message> {
        val serializationContext = RedisSerializationContext
            .newSerializationContext<String, Message>(StringRedisSerializer())
            .key(StringRedisSerializer())
            .value(Jackson2JsonRedisSerializer(Message::class.java). apply {
                this.setObjectMapper(objectMapper)
            })
            .build()
        return ReactiveRedisTemplate(lettuceConnectionFactory, serializationContext)
    }
}