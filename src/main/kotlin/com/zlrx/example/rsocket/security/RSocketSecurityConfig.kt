package com.zlrx.example.rsocket.security

import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
class RSocketSecurityConfig {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun strategyCustomizer(): RSocketStrategiesCustomizer = RSocketStrategiesCustomizer {
        it.encoder(SimpleAuthenticationEncoder())
    }

    @Bean
    fun rSocketMessageHandler(strategy: RSocketStrategies) = RSocketMessageHandler().apply {
        rSocketStrategies = strategy
        argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
    }

    @Bean
    fun payloadSocketAcceptorInterceptor(security: RSocketSecurity) = security
        .simpleAuthentication(Customizer.withDefaults())
        .authorizePayload {
            it
                //.setup().permitAll()
                .setup().hasRole("TRUSTED_CLIENT")
                //.route("secured.request-stream").hasRole("ADMIN")
                //.anyRequest().hasAnyRole("USER")
                .anyRequest().authenticated()

        }.build()
}