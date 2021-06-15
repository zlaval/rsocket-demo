package com.zlrx.example.rsocket.router

import com.zlrx.example.rsocket.model.ComputationRequest
import com.zlrx.example.rsocket.service.ComputationService
import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
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

    @MessageMapping("validate-input.{input}")
    suspend fun validateInput(@DestinationVariable input: Int, requester: RSocketRequester) = if (input < 10) {
        input * 2
    } else {
        throw IllegalArgumentException("Input must be less than 10")
    }

    @MessageExceptionHandler
    suspend fun exceptionHandler(exception: Exception): Int = 777


}