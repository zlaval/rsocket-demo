package com.zlrx.example.rsocket.native

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.rsocket.ConnectionSetupPayload
import io.rsocket.Payload
import io.rsocket.RSocket
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketServer
import io.rsocket.transport.netty.server.TcpServerTransport
import io.rsocket.util.DefaultPayload
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

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

data class ChartResponse(
    val input: Int,
    val output: Int
) {
    private fun getFormat(value: Int): String {
        return "%3s|%${value}s"
    }

    override fun toString(): String {
        return String.format(getFormat(output), input, "X")
    }
}

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
        return Mono.fromCallable {
            RSocketService()
        }
    }
}

class RSocketService : RSocket {

    override fun fireAndForget(payload: Payload): Mono<Void> {
        println(fromPayload<Request>(payload))
        return Mono.empty()
    }

    override fun requestResponse(payload: Payload): Mono<Payload> {
        return Mono.fromSupplier {
            val request: Request = fromPayload(payload)
            val response = Response(request.input, request.input * 10)
            toPayload(response)
        }
    }

    override fun requestStream(payload: Payload): Flux<Payload> {
        val request: Request = fromPayload(payload)
        return Flux.range(1, 10)
            .map {
                request.input * it
            }.map {
                Response(request.input, it)
            }.delayElements(Duration.ofSeconds(1))
            .map { toPayload(it) }
            .doOnNext { println(it) }
    }

    override fun requestChannel(payloads: Publisher<Payload>): Flux<Payload> {
        return Flux.from(payloads)
            .map {
                val request = fromPayload<Request>(it)
                val input: Int = request.input
                val result = ChartResponse(input, input * input + 1)
                toPayload(result)
            }
    }
}