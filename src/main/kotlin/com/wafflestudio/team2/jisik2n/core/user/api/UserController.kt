package com.wafflestudio.team2.jisik2n.core.user.api

import com.wafflestudio.team2.jisik2n.core.user.api.request.SignupRequest
import com.wafflestudio.team2.jisik2n.core.user.service.AuthToken
import com.wafflestudio.team2.jisik2n.core.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/users")
    fun getUsers(): String {
        return "Hello World"
    }

    @PostMapping("signup")
    fun signup(@RequestBody @Valid request: SignupRequest): AuthToken {
        return userService.signup(request)
    }

}
