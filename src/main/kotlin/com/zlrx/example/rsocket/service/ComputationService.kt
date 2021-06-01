package com.zlrx.example.rsocket.service

import com.zlrx.example.rsocket.domain.ComputationRequest
import com.zlrx.example.rsocket.domain.ComputationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ComputationService {

    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun fireAndForget(request: ComputationRequest) {
        logger.info(request.toString())
    }

    suspend fun requestResponse(request: ComputationRequest): ComputationResponse = ComputationResponse(request.input, request.input * 7)

    suspend fun requestStream(request: ComputationRequest): Flow<ComputationResponse> = flow {
        (0 until request.input).forEach { index ->
            emit(ComputationResponse(request.input, request.input * index))
        }
    }

    suspend fun requestChannel(stream: Flow<ComputationRequest>) = stream.map { ComputationResponse(it.input, it.input.times(it.input)) }

}