package com.wafflestudio.team2.jisik2n

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class Jisik2nApplication
fun main(args: Array<String>) {
    runApplication<Jisik2nApplication>(*args)
}
