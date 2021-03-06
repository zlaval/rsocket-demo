package com.zlrx.example.rsocket.integration

import com.zlrx.example.rsocket.model.ComputationRequest
import com.zlrx.example.rsocket.model.ComputationResponse
import io.rsocket.transport.netty.client.TcpClientTransport
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import reactor.test.StepVerifier

@SpringBootTest
class ComputationTest {

    @Autowired
    private lateinit var rSocketBuilder: RSocketRequester.Builder

    @Autowired
    private lateinit var handler: RSocketMessageHandler

    private lateinit var requester: RSocketRequester

    @BeforeEach
    fun init() {
        requester = rSocketBuilder
            .rsocketConnector {
                it.acceptor(handler.responder())
            }
            .transport(TcpClientTransport.create("localhost", 6555))
    }


    @Test
    fun testFireAndForget() {
        val result = requester.route("computation.fire-and-forget")
            .data(ComputationRequest(10))
            .send()
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun testPrintInput() {
        val result = requester.route("computation.fire-and-forget.15")
            .data(ComputationRequest(10))
            .send()
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun testRequestResponse() {
        val result = requester.route("computation.request-response")
            .data(ComputationRequest(10))
            .retrieveMono(ComputationResponse::class.java)
        StepVerifier.create(result)
            .expectNext(ComputationResponse(10, 70))
            .verifyComplete()
    }

    @Test
    fun testRequestStream() {
        val result = requester.route("computation.request-stream")
            .data(ComputationRequest(10))
            .retrieveFlux(ComputationResponse::class.java)
        StepVerifier.create(result)
            .expectNextCount(10)
            .verifyComplete()
    }

    @Test
    fun testRequestChannel() {
        val payload = flow {
            (0 until 10).forEach {
                emit(ComputationRequest(it))
            }

        }
        val result = requester.route("computation.request-channel")
            .data(payload)
            .retrieveFlux(ComputationResponse::class.java)
        StepVerifier.create(result)
            .expectNextCount(10)
            .verifyComplete()
    }

    @Test
    fun testInputValidation() {
//        val result = requester.route("computation.validate-input.10")
//            .retrieveMono(Int::class.java)
//        StepVerifier.create(result)
//            .verifyError(ApplicationErrorException::class.java)

        val result2 = requester.route("computation.validate-input.9")
            .retrieveMono(Int::class.java)
            .doOnNext {
                println(it)
            }
        StepVerifier.create(result2)
            .expectNextCount(1)
            .verifyComplete()
    }

}