package com.zlrx.example.rsocket.service

import com.zlrx.example.rsocket.security.MockUserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsServiceImpl(
    private val repository: MockUserRepository
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> = repository.findByUserName(username)

}