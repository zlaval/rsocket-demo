package com.zlrx.example.rsocket.router

import com.zlrx.example.rsocket.model.ClientConnectionRequest
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class ConnectionSetupExample {

    @ConnectMapping
    fun handleConnection(request: ClientConnectionRequest, requester: RSocketRequester): Mono<Void> {
        println("connect $request")
        //return if (request.secret == "password") Mono.empty() else Mono.error(RuntimeException("Invalid secret"))
        return if (request.secret == "password")
            Mono.empty()
        else
            Mono.fromRunnable {
                requester.rsocketClient().dispose()
            }
    }

}