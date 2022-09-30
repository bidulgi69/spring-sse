package kr.dove.communication.sse

import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import javax.annotation.PostConstruct

@CrossOrigin(originPatterns = ["*"], allowedHeaders = ["*"], allowCredentials = "true")
@RestController
class StockController {

    private val tickers: MutableMap<String, Float> = mutableMapOf()
    private val prices: MutableMap<String, Flux<Float>> = mutableMapOf()

    @PostConstruct
    fun init() {
        tickers["TSLA"] = 268.21f
        tickers["PSHG"] = 0.2394f
        tickers["SOXS"] = 67.09f

        prices["TSLA"] = Flux.interval(Duration.ofSeconds(2))
            .flatMap {
                val rand = Math.random() * 10 > 5.0
                val tsla = (Math.random() * 100f).toFloat() / 10f
                tickers["TSLA"] = tickers["TSLA"]!! + (if (rand) tsla else -tsla)

                Mono.just(tickers["TSLA"]!!)
            }

        prices["PSHG"] = Flux.interval(Duration.ofSeconds(2))
            .flatMap {
                val rand = Math.random() * 10 > 5.0
                val pshg = (Math.random() * 1000f).toFloat() / 10000f
                tickers["PSHG"] = tickers["PSHG"]!! + (if (rand) pshg else -pshg)
                Mono.just(tickers["PSHG"]!!)
            }

        prices["SOXS"] = Flux.interval(Duration.ofSeconds(1))
            .flatMap {
                val rand = Math.random() * 10 > 5.0
                val soxs = (Math.random() * 100f).toFloat() / 100f
                tickers["SOXS"] = tickers["SOXS"]!! + (if (rand) soxs else -soxs)
                Mono.just(tickers["SOXS"]!!)

            }
    }

    @GetMapping(
        value = ["/connect/{ticker}"]
    )
    fun connect(@PathVariable(name = "ticker") ticker: String): Flux<ServerSentEvent<Float>> {
        return prices[ticker] ?. let { flux ->
            flux
                .flatMap { v ->
                    Mono.just(
                        ServerSentEvent
                            .builder(v)
                            .event("tick")
                            .build()
                    )
                }
        } ?: Flux.empty()
    }

    @GetMapping(
        value = ["/tickers"],
        produces = ["application/json"]
    )
    fun tickers(): Flux<String> {
        return Flux.fromIterable(
            tickers.keys
        ).flatMap { ticker ->
            Mono.just("$ticker,")
        }
    }
}