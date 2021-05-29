package com.zlrx.example.rsocket.native

import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier


class CallbackService : RSocket {

    override fun fireAndForget(payload: Payload): Mono<Void> {
        val response = fromPayload<Response>(payload)
        println("Client received  $response")
        return Mono.empty()
    }
}

class BatchTest {
    private val rSocket = RSocketConnector.create()
        .acceptor(SocketAcceptor.with(CallbackService()))
        .connect(TcpClientTransport.create("localhost", 6666))
        .block()!!


    @Test
    fun `fire and forget with object`() {
        val message = toPayload(Request(7))
        val stream = rSocket.fireAndForget(message)

        StepVerifier.create(stream)
            .verifyComplete()
        Thread.sleep(4000)
    }
}