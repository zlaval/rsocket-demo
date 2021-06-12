package com.zlrx.example.rsocket.integration

import com.zlrx.example.rsocket.model.ComputationRequest
import com.zlrx.example.rsocket.model.ComputationResponse
import io.rsocket.metadata.WellKnownMimeType
import io.rsocket.transport.netty.client.TcpClientTransport
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata
import org.springframework.util.MimeTypeUtils
import reactor.test.StepVerifier

@SpringBootTest
class SecuredTest {

    @Autowired
    private lateinit var rSocketBuilder: RSocketRequester.Builder

    private lateinit var requester: RSocketRequester

    @BeforeEach
    fun init() {
        val metadata = UsernamePasswordMetadata("client", "password")
        requester = rSocketBuilder
            .setupMetadata(metadata, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.string))
            .transport(TcpClientTransport.create("localhost", 6555))
    }

    @Test
    fun testRequestResponse() {
        val result = requester.route("secured.request-response")
            .data(ComputationRequest(10))
            .retrieveMono(ComputationResponse::class.java)
        StepVerifier.create(result)
            .expectNext(ComputationResponse(10, 70))
            .verifyComplete()
    }

    @Test
    fun testRequestStream() {
        val result = requester.route("secured.request-stream")
            .data(ComputationRequest(10))
            .retrieveFlux(ComputationResponse::class.java)
        StepVerifier.create(result)
            .expectNextCount(10)
            .verifyComplete()
    }

}