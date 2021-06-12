package com.zlrx.example.rsocket.integration

import com.zlrx.example.rsocket.model.ClientConnectionRequest
import com.zlrx.example.rsocket.model.ComputationRequest
import io.rsocket.transport.netty.client.TcpClientTransport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester


@SpringBootTest
class ConnectionSetupExample {

    @Autowired
    private lateinit var rSocketBuilder: RSocketRequester.Builder

//    private lateinit var requester: RSocketRequester
//
//
//    @BeforeEach
//    fun init() {
//        val request = ClientConnectionRequest("my-id", "password")
//        requester = rSocketBuilder
//            .setupData(request)
//            .transport(TcpClientTransport.create("localhost", 6555))
//
//    }
//
//    @Test
//    fun testConnection() {
//        val response = requester.route("computation.request-response")
//            .data(ComputationRequest(10))
//            .retrieveMono(ComputationResponse::class.java)
//            .doOnNext { println(it) }
//
//        StepVerifier.create(response)
//            .expectNextCount(1)
//            .verifyComplete()
//    }


//    companion object {
//        init {
//            System.setProperty("javax.net.ssl.trustStore", "/ssl/client.truststore")
//            System.setProperty("javax.net.ssl.trustStorePassword", "password")
//        }
//    }
//
//    @Test
//    fun testSSLConnection() {
//        val requester = rSocketBuilder
//            .transport(
//                TcpClientTransport.create(
//                    TcpClient.create()
//                        .host("localhost")
//                        .port(6555)
//                        .secure()
//                )
//            )
//
//        val response = requester.route("computation.request-response")
//            .data(ComputationRequest(10))
//            .retrieveMono(ComputationResponse::class.java)
//            .doOnNext { println(it) }
//
//        StepVerifier.create(response)
//            .expectNextCount(1)
//            .verifyComplete()
//    }


    @Test
    fun testManager() {
        val request = ClientConnectionRequest("my-id", "password")

        val requester1 = rSocketBuilder
            .setupData(request)
            .transport(TcpClientTransport.create("localhost", 6555))

        val requester3 = rSocketBuilder
            .setupData(request)
            .transport(TcpClientTransport.create("localhost", 6555))

        val requester2 = rSocketBuilder
            .setupRoute("computation.fire-and-forget")
            .setupData(request)
            .transport(TcpClientTransport.create("localhost", 6555))


        val first = requester1.route("computation.fire-and-forget").data(ComputationRequest(10)).send().subscribe()
        requester2.route("computation.fire-and-forget").data(ComputationRequest(10)).send().subscribe()
        requester3.route("computation.request-response").data(ComputationRequest(10)).send().subscribe()

        Thread.sleep(3000)
        println("Dispose 2 client")
        first.dispose()
        requester1.rsocketClient().dispose()
        requester2.rsocketClient().dispose()
        Thread.sleep(5000)

    }

}