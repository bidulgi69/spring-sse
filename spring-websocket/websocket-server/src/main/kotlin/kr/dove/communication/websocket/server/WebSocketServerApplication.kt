package kr.dove.communication.websocket.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class WebSocketServerApplication {

	@Bean
	fun objectMapper(): ObjectMapper {
		return ObjectMapper()
			.registerModule(JavaTimeModule())
	}
}

fun main(args: Array<String>) {
	runApplication<WebSocketServerApplication>(*args)
}
