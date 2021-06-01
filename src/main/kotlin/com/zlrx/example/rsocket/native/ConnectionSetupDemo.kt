package com.zlrx.example.rsocket.native

import io.rsocket.ConnectionSetupPayload
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketServer
import io.rsocket.transport.netty.server.TcpServerTransport
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun main() {
    val server = RSocketServer.create(ServiceAcceptor())
    val channel = server.bindNow(TcpServerTransport.create(6555))
    channel.onClose().block()
}

class ServiceAcceptor : SocketAcceptor {
    override fun accept(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        val acceptor = if (isValidClient(setup.dataUtf8)) RSocketService() else FreeService()
        return Mono.just(acceptor)
    }

    private fun isValidClient(credentials: String): Boolean {
        return "username:password" == credentials
    }
}

class FreeService : RSocket {
    override fun fireAndForget(payload: Payload): Mono<Void> {
        return Mono.empty()
    }

    override fun requestResponse(payload: Payload): Mono<Payload> {
        return Mono.empty()
    }

    override fun requestStream(payload: Payload): Flux<Payload> {
        return Flux.empty()
    }

    override fun requestChannel(payloads: Publisher<Payload>): Flux<Payload> {
        return Flux.empty()
    }
}