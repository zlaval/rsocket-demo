package com.zlrx.example.rsocket.native

import io.rsocket.ConnectionSetupPayload
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketServer
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.DefaultPayload
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun main() {
    val server = RSocketServer.create(StreamAcceptor())
    val channel = server.bindNow(TcpServerTransport.create(6555))

    channel.onClose().block()
}

class StreamAcceptor : SocketAcceptor {
    override fun accept(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        return Mono.fromCallable {
            FastProducer()
        }
    }
}

class FastProducer : RSocket {

    override fun requestStream(payload: Payload): Flux<Payload> {
        return Flux.range(1, 1000)
            .map {
                "$it. item"
            }.doOnNext {
                println("$it, ")
            }.map {
                DefaultPayload.create(it)
            }.doFinally {
                println("End of producing")
            }.doOnCancel { println("Stream cancelled") }
    }
}