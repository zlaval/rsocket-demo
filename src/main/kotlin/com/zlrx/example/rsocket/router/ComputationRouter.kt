package com.zlrx.example.rsocket.router

import com.zlrx.example.rsocket.domain.ComputationRequest
import com.zlrx.example.rsocket.service.ComputationService
import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class ComputationRouter(
    private val service: ComputationService
) {

    @MessageMapping("computation.fire-and-forget")
    suspend fun callFireAndForget(request: ComputationRequest) = service.fireAndForget(request)

    @MessageMapping("computation.request-response")
    suspend fun callRequestResponse(request: ComputationRequest) = service.requestResponse(request)

    @MessageMapping("computation.request-stream")
    suspend fun callRequestStream(request: ComputationRequest) = service.requestStream(request)

    @MessageMapping("computation.request-channel")
    suspend fun callRequestChannel(payload: Flow<ComputationRequest>) = service.requestChannel(payload)

}