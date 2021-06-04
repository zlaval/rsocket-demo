package com.zlrx.example.rsocket.service

import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.Collections

@Service
class ClientManager {

    private val requesters = Collections.synchronizedSet(HashSet<RSocketRequester>())

    fun registerClient(requester: RSocketRequester) {
        val socket = requester.rsocket()!!
        socket
            .onClose()
            .doFirst {
                requesters.add(requester)
            }
            .doFinally {
                requesters.remove(requester)
            }
            .subscribe()
    }

    @Scheduled(fixedRate = 1000)
    fun printSize() {
        println("Connected clients: ${requesters.size}")
    }


}