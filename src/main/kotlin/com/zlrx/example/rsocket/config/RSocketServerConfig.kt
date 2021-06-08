package com.zlrx.example.rsocket.config

import io.rsocket.core.Resume
import io.rsocket.loadbalance.LoadbalanceTarget
import io.rsocket.transport.netty.client.TcpClientTransport
import org.springframework.boot.rsocket.server.RSocketServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Configuration
class RSocketServerConfig {

    @Bean
    fun rSocketServerCustomizer(): RSocketServerCustomizer = RSocketServerCustomizer {
        it.resume(Resume().sessionDuration(Duration.ofSeconds(10)))
    }

    @Bean
    fun requesters(): Flux<List<LoadbalanceTarget>> = Flux.from(clientSideLoadBalancing())//TODO request from clients sometime

    //@Bean
    fun clientSideLoadBalancing(): Mono<List<LoadbalanceTarget>> {
        //get instances from service registry and create from all
        val oneClient = LoadbalanceTarget.from("service-unique-name", TcpClientTransport.create("localhost", 6555))
        return Mono.just(listOf(oneClient))
        //TODO use Flux(Mono(List(LoadbalanceTarget))) for update clients
    }


}