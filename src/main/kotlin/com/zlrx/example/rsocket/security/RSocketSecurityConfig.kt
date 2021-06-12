package com.zlrx.example.rsocket.security

import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder

@Configuration
@EnableRSocketSecurity
class RSocketSecurityConfig {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun strategyCustomizer(): RSocketStrategiesCustomizer = RSocketStrategiesCustomizer {
        it.encoder(SimpleAuthenticationEncoder())
    }

    @Bean
    fun payloadSocketAcceptorInterceptor(security: RSocketSecurity) = security
        .simpleAuthentication(Customizer.withDefaults())
        .authorizePayload {
            it
                //.setup().permitAll()
                .setup().hasRole("TRUSTED_CLIENT")
                .anyRequest().permitAll()
        }.build()
}