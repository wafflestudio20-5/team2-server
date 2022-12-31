package com.wafflestudio.team2.jisik2n.core.user.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.user.api.request.LoginRequest
import com.wafflestudio.team2.jisik2n.core.user.api.request.SignupRequest
import com.wafflestudio.team2.jisik2n.core.user.service.AuthToken
import com.wafflestudio.team2.jisik2n.core.user.service.UserService
import org.springframework.web.bind.annotation.*
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

    @PostMapping("login")
    fun login(@RequestBody request: LoginRequest): AuthToken {
        return userService.login(request)
    }

    @Authenticated
    @GetMapping("validate")
    fun validate(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("RefreshToken") refreshToken: String,
        @UserContext userId: Long
    ): AuthToken {

        return userService.validate(userId)
    }

//    @Authenticated
//    @GetMapping("reissue/{uid}")
//    fun reissue(@PathVariable uid: String, @RequestHeader("Authorization") refreshToken: String) {
//        userService.reissue(uid, refreshToken)
//    }
}
