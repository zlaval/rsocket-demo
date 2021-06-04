package com.zlrx.example.rsocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class RSocketApplication

fun main(args: Array<String>) {
    runApplication<RSocketApplication>(*args)
}
