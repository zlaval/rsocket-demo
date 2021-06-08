package com.zlrx.example.rsocket.config

import io.rsocket.core.Resume
import org.springframework.boot.rsocket.server.RSocketServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RSocketServerConfig {

    @Bean
    fun rSocketServerCustomizer(): RSocketServerCustomizer = RSocketServerCustomizer {
        it.resume(Resume().sessionDuration(Duration.ofSeconds(10)))
    }


}