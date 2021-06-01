package com.zlrx.example.rsocket.native

import io.rsocket.core.RSocketClient
import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


class ConnectionSetupTest {
    private val rSocketMono = RSocketConnector.create()
        .setupPayload(DefaultPayload.create("username:password"))
        .connect(TcpClientTransport.create("localhost", 6555))
        .doOnNext { println("Connect to server") }

    private val rSocketClient = RSocketClient.from(rSocketMono)


    @Test
    fun `request stream`() {
        val request = Mono.just(toPayload(Request(10)))
        val result = rSocketClient.requestStream(request)
            .map { fromPayload<Response>(it) }
            .doOnEach { println(it) }
        StepVerifier.create(result)
            .expectSubscription()
            .expectNextCount(10)
            .verifyComplete()
    }

}