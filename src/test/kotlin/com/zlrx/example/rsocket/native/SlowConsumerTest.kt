package com.zlrx.example.rsocket.native

import io.rsocket.core.RSocketClient
import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

class SlowConsumerTest {

    private val rSocketMono = RSocketConnector.create()
        .connect(TcpClientTransport.create("localhost", 6555))
        .doOnNext { println("Connect to server") }

    private val rSocketClient = RSocketClient.from(rSocketMono)

    @Test
    fun slowConsumer() {

        val result = rSocketClient
            .requestStream(Mono.just(DefaultPayload.create("")))
            .map {
                it.dataUtf8
            }
            .delayElements(Duration.ofMillis(200))
            .doOnNext {
                println("Consumed $it")
            }

        StepVerifier.create(result)
            .expectNextCount(1000)
            .verifyComplete()

    }

    @Test
    fun persistentConnection() {

        val result = rSocketClient.requestStream(Mono.just(DefaultPayload.create("")))
            .map {
                it.dataUtf8
            }
            .delayElements(Duration.ofMillis(200))
            .take(10)
            .doOnNext {
                println("Consumed $it")
            }

        StepVerifier.create(result)
            .expectNextCount(10)
            .verifyComplete()

        Thread.sleep(10_000)
        println("wake up")


        val result2 = rSocketClient.requestStream(Mono.just(DefaultPayload.create("")))
            .map {
                it.dataUtf8
            }
            .delayElements(Duration.ofMillis(200))
            .take(10)
            .doOnNext {
                println("Consumed $it")
            }

        StepVerifier.create(result2)
            .expectNextCount(10)
            .verifyComplete()

    }

}