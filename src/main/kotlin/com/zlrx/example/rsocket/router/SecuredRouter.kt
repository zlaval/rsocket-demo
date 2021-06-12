package com.zlrx.example.rsocket.router

import com.zlrx.example.rsocket.model.ComputationRequest
import com.zlrx.example.rsocket.service.ComputationService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("secured")
class SecuredRouter(
    private val service: ComputationService
) {

    @MessageMapping("request-response")
    suspend fun callRequestResponse(request: ComputationRequest) = service.requestResponse(request)

    @MessageMapping("request-stream")
    suspend fun callRequestStream(request: ComputationRequest) = service.requestStream(request)


}