package com.zlrx.example.rsocket.router

import com.zlrx.example.rsocket.domain.ComputationRequest
import com.zlrx.example.rsocket.service.ComputationService
import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("computation")
class ComputationRouter(
    private val service: ComputationService
) {

    @MessageMapping("fire-and-forget")
    suspend fun callFireAndForget(request: ComputationRequest) = service.fireAndForget(request)

    @MessageMapping("request-response")
    suspend fun callRequestResponse(request: ComputationRequest) = service.requestResponse(request)

    @MessageMapping("request-stream")
    suspend fun callRequestStream(request: ComputationRequest) = service.requestStream(request)

    @MessageMapping("request-channel")
    suspend fun callRequestChannel(payload: Flow<ComputationRequest>) = service.requestChannel(payload)

    @MessageMapping("fire-and-forget.{input}")
    suspend fun processInput(@DestinationVariable input: Int) = service.printInput(input)

}