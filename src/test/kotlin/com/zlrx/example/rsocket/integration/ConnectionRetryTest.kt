package com.zlrx.example.rsocket.integration

import com.zlrx.example.rsocket.model.ComputationRequest
import com.zlrx.example.rsocket.model.ComputationResponse
import io.rsocket.transport.netty.client.TcpClientTransport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.test.context.TestPropertySource
import reactor.test.StepVerifier
import reactor.util.retry.Retry
import java.time.Duration

@SpringBootTest
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration"])
class ConnectionRetryTest {

    @Autowired
    private lateinit var rSocketBuilder: RSocketRequester.Builder

    @Test
    fun testConnectionRetry() {
        val requester = rSocketBuilder
            .rsocketConnector { it.reconnect(Retry.fixedDelay(10, Duration.ofSeconds(2)).doBeforeRetry { println("Retry") }) }
            .transport(TcpClientTransport.create("localhost", 6555))

        for (i in 0..50) {
            val result = requester.route("computation.request-response")
                .data(ComputationRequest(10))
                .retrieveMono(ComputationResponse::class.java)
                .doOnNext { println(it) }

            StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete()

            Thread.sleep(1000)
        }

    }

}