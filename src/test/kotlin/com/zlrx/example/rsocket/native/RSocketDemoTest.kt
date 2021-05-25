package com.zlrx.example.rsocket.native

import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

class RSocketDemoTest {

    private val rSocket = RSocketConnector.create()
        .connect(TcpClientTransport.create("localhost", 6555))
        .block()!!

    //@RepeatedTest(3)
    fun `run fire and forget`() {
        val message = DefaultPayload.create("Hello world")
        val stream = rSocket.fireAndForget(message)

        StepVerifier.create(stream)
            .verifyComplete()
    }

    @Test
    fun `fire and forget with object`() {
        val message = toPayload(Request(7))
        val stream = rSocket.fireAndForget(message)

        StepVerifier.create(stream)
            .verifyComplete()
    }

    @Test
    fun `request response`() {
        val request = toPayload(Request(10))
        val result = rSocket.requestResponse(request).map { fromPayload<Response>(it) }
        StepVerifier.create(result)
            .expectSubscription()
            .expectNext(Response(10, 100))
            .verifyComplete()
    }

    @Test
    fun `request stream`() {
        val request = toPayload(Request(10))
        val result = rSocket.requestStream(request).map { fromPayload<Response>(it) }
        StepVerifier.create(result)
            .expectSubscription()
            .expectNextCount(10)
            .verifyComplete()
    }

    @Test
    fun `request channel`() {
        val publisher = Flux.range(-10, 21)
            .map { Request(it) }
            .delayElements(Duration.ofMillis(200))
            .map { toPayload(it) }

        val result = rSocket.requestChannel(publisher).map { fromPayload<ChartResponse>(it) }
            .doOnNext {
                println(it)
            }

        StepVerifier.create(result)
            .expectSubscription()
            .expectNextCount(21)
            .verifyComplete()
    }

}