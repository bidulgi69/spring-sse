package kr.dove.communication.websocket.server

import kr.dove.communication.api.Message
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Configuration
class WebSocketConfiguration(
    private val webSocketHandler: WebSocketHandler,
) {

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = 1
        handlerMapping.urlMap = mutableMapOf<String, WebSocketHandler>().apply {
            put("/connect", webSocketHandler)
        }
        return handlerMapping
    }
}

@Component
class ReactiveWebSocketHandler(
    lettuceConnectionFactory: ReactiveRedisConnectionFactory,
    private val messageRedisTemplate: ReactiveRedisTemplate<String, Message>,
    private val objectMapper: ObjectMapper,
    @Value("\${redis.channel.topic.qna}") topic: String,
) : WebSocketHandler {
    private val channel = ChannelTopic(topic)
    private var reactiveRedisMessageListenerContainer = ReactiveRedisMessageListenerContainer(lettuceConnectionFactory)

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session
            .send(
                reactiveRedisMessageListenerContainer
                    .receive(channel)
                    .flatMap {
                        Mono.just(
                            session.textMessage(
                                objectMapper.writeValueAsString(it.message)
                            )
                        )
                    }
            )
            .and(
                session.receive()
                    .flatMap { socketMsg ->
                        val message = objectMapper.readValue(
                            socketMsg.payloadAsText,
                            Message::class.java
                        )

                        messageRedisTemplate
                            .convertAndSend(
                                channel.topic,
                                message
                            )
                    }
                    .log()
            )
    }
}

