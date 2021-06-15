package com.zlrx.example.rsocket.router

import com.zlrx.example.rsocket.model.ComputationRequest
import com.zlrx.example.rsocket.model.ComputationResponse
import com.zlrx.example.rsocket.service.ComputationService
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("secured")
class SecuredRouter(
    private val service: ComputationService
) {

    @MessageMapping("request-response")
    suspend fun callRequestResponse(
        request: ComputationRequest,
        @AuthenticationPrincipal user: UserDetails,
        @Header("operation-type") operation: String
    ): ComputationResponse {
        println("$operation $user")
        return service.requestResponse(request)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MessageMapping("request-stream")
    suspend fun callRequestStream(request: ComputationRequest) = service.requestStream(request)


}