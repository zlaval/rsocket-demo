package com.zlrx.example.rsocket.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class MockUserRepository(
    passwordEncoder: PasswordEncoder
) {

    private val userDatabase = mapOf<String, UserDetails>(
        "admin" to User.withUsername("admin").password(passwordEncoder.encode("password")).roles("ADMIN").build(),
        "user" to User.withUsername("user").password(passwordEncoder.encode("password")).roles("USER").build(),
        "client" to User.withUsername("client").password(passwordEncoder.encode("password")).roles("TRUSTED_CLIENT").build()
    )

    fun findByUserName(userName: String) = Mono.just(userDatabase[userName] ?: throw RuntimeException("User not found"))

}