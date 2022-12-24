package com.wafflestudio.team2.jisik2n.helloWorld

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test")
class HelloWorldController {
    @GetMapping
    fun getHelloWorld() = ResponseEntity<String>("Hello, World!", HttpStatus.OK)
}