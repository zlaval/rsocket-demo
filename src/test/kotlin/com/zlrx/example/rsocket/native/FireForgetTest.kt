package com.zlrx.example.rsocket.native

import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.util.DefaultPayload
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class FireForgetTest {

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

}