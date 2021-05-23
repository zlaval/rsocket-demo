package com.zlrx.example.rsocket.native

import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

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

}