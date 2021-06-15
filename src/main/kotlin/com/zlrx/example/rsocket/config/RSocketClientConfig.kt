package com.zlrx.example.rsocket.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer
import reactor.util.retry.Retry
import java.time.Duration

@Configuration
class RSocketClientConfig {

    @Bean
    fun connectorConfigurer() = RSocketConnectorConfigurer { connector ->
        connector.reconnect(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2)).doBeforeRetry { println("Try to reconnect ${it.totalRetries()}. times") })
    }

}