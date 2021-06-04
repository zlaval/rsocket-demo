package com.zlrx.example.rsocket.router

import com.zlrx.example.rsocket.model.ClientConnectionRequest
import com.zlrx.example.rsocket.service.ClientManager
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class ConnectionSetupExample(
    private val clientManager: ClientManager
) {

    @ConnectMapping("computation.fire-and-forget")
    fun handleConnectionFireAndForget(request: ClientConnectionRequest, requester: RSocketRequester): Mono<Void> {
        println("connect fire and forget $request")

        //return if (request.secret == "password") Mono.empty() else Mono.error(RuntimeException("Invalid secret"))


//        return if (request.secret == "password")
//            Mono.empty()
//        else
//            Mono.fromRunnable {
//                requester.rsocketClient().dispose()
//            }


        return Mono.fromRunnable {
            clientManager.registerClient(requester)
        }
    }


    @ConnectMapping
    fun handleConnection(requester: RSocketRequester): Mono<Void> {
        println("connect without event")
        return Mono.empty()
    }

}