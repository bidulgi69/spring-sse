package kr.dove.communication.websocket.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import kr.dove.communication.api.Message
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration
import java.util.UUID
import kotlin.system.exitProcess


fun main(args: Array<String>) {
	val host = System.getProperty("socket-host") ?: "localhost"
	val port = System.getProperty("socket-port") ?: "8080"

	val webSocketUri = "ws://$host:$port/connect"
	val objectMapper = ObjectMapper().apply {
		registerModule(JavaTimeModule())
	}
	val client1 = ReactorNettyWebSocketClient()
	val client2 = ReactorNettyWebSocketClient()

	val client1Messages = Flux.interval(Duration.ofSeconds(2L))
	val client2Messages = Flux.interval(Duration.ofSeconds(5L))


	val connection1 = client1.execute(
		URI.create(webSocketUri)
	) { session ->
		session.send(
			client1Messages.flatMap {
				Mono.just(
					session.textMessage(
						objectMapper.writeValueAsString(
							Message(
								UUID.randomUUID().toString(),
								"client1",
								"Message-$it from client1"
							)
						)
					)
				)
			}
		)
			.and(
				session.receive()
					.flatMap { socketMsg ->
						Mono.just(
							"(Client1) Received from the server::: ${socketMsg.payloadAsText}"
						)
					}.log()
			)
	}

	val connection2 = client2.execute(
		URI.create(webSocketUri)
	) { session ->
		session.send(
			client2Messages.flatMap {
				Mono.just(
					session.textMessage(
						objectMapper.writeValueAsString(
							Message(
								UUID.randomUUID().toString(),
								"client2",
								"Message-$it from client2"
							)
						)
					)
				)
			}
		)
			.and(
				session.receive()
					.flatMap { socketMsg ->
						Mono.just(
							"(Client2) Received from the server::: ${socketMsg.payloadAsText}"
						)
					}.log()
			)
	}

	try {
		Mono.zip(
			connection1, connection2
		).block(
			Duration.ofSeconds(20L)
		)
	} catch (ignored: IllegalStateException) {
		exitProcess(1)
	}
}