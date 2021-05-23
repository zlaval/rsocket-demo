package com.zlrx.example.rsocket.native

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.rsocket.ConnectionSetupPayload
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketServer
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.DefaultPayload
import reactor.core.publisher.Mono

fun main() {
    val server = RSocketServer.create(SocketAcceptorImpl())
    val channel = server.bindNow(TcpServerTransport.create(6555))
    channel.onClose().block()
}

val objectMapper = jacksonObjectMapper()


data class Request(
    val input: Int
)

data class Response(
    val input: Int,
    val output: Int
)

fun <T> toPayload(obj: T): Payload {
    val bytes = objectMapper.writeValueAsBytes(obj)
    return DefaultPayload.create(bytes)
}

inline fun <reified T> fromPayload(payload: Payload): T {
    val bytes = payload.data.array()
    return objectMapper.readValue(bytes, T::class.java)
}

class SocketAcceptorImpl : SocketAcceptor {

    override fun accept(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {
        println("Accept")
        return Mono.fromCallable {
            MathService()
        }
    }

}


class MathService : RSocket {

    override fun fireAndForget(payload: Payload): Mono<Void> {
        println(fromPayload<Request>(payload))
        return Mono.empty()
    }
}