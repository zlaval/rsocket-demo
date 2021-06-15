package com.zlrx.example.rsocket.service

import io.rsocket.transport.netty.client.TcpClientTransport
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ClientRequester constructor(
    requesterBuilder: RSocketRequester.Builder,
    configurer: RSocketConnectorConfigurer,
) {

    private val requester: RSocketRequester = requesterBuilder.rsocketConnector(configurer)
        .transport(TcpClientTransport.create("localhost", 6677))

    fun getData(): Mono<String> = requester.route("test.data").retrieveMono(String::class.java)

}