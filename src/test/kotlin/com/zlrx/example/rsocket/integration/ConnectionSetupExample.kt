package com.zlrx.example.rsocket.integration

import com.zlrx.example.rsocket.model.ClientConnectionRequest
import com.zlrx.example.rsocket.model.ComputationRequest
import com.zlrx.example.rsocket.model.ComputationResponse
import io.rsocket.transport.netty.client.TcpClientTransport
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.test.StepVerifier

@SpringBootTest
class ConnectionSetupExample {

    @Autowired
    private lateinit var rSocketBuilder: RSocketRequester.Builder

    private lateinit var requester: RSocketRequester

    @BeforeEach
    fun init() {
        val request = ClientConnectionRequest("my-id", "password")
        requester = rSocketBuilder
            .setupData(request)
            .transport(TcpClientTransport.create("localhost", 6555))
    }

    @Test
    fun testConnection() {
        val response = requester.route("computation.request-response")
            .data(ComputationRequest(10))
            .retrieveMono(ComputationResponse::class.java)
            .doOnNext { println(it) }

        StepVerifier.create(response)
            .expectNextCount(1)
            .verifyComplete()
    }

}