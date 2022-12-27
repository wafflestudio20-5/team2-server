package com.wafflestudio.team2.jisik2n.core.user.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
    @GetMapping("/users")
    fun getUsers(): String {
        return "Hello World"
    }
}