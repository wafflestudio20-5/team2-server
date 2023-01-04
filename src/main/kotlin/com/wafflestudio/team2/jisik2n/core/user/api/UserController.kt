package com.wafflestudio.team2.jisik2n.core.user.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.LoginRequest
import com.wafflestudio.team2.jisik2n.core.user.dto.SignupRequest
import com.wafflestudio.team2.jisik2n.core.user.dto.TokenRequest
import com.wafflestudio.team2.jisik2n.core.user.service.AuthToken
import com.wafflestudio.team2.jisik2n.core.user.service.UserService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {
    @GetMapping("users")
    fun getUsers(): String {
        return "Hello World"
    }

    @PostMapping("signup")
    fun signup(@RequestBody @Valid signupRequest: SignupRequest): AuthToken {
        return userService.signup(signupRequest)
    }

    @PostMapping("signupCheckDuplicatedUid")
    fun signupCheckDuplicatedUid(@RequestBody request: Map<String, String>) {
        return userService.signupCheckDuplicatedUid(request)
    }

    @PostMapping("login")
    fun login(@RequestBody loginRequest: LoginRequest): AuthToken {
        return userService.login(loginRequest)
    }

    @PostMapping("logout")
    fun logout(@RequestBody token: TokenRequest): String {
        return userService.logout(token)
    }

    @GetMapping("getKakaoUserInfo")
    fun getKakaoUserInfo(@RequestParam(value = "accessToken", required = false) accessToken: String): AuthToken {

        val userInfo: HashMap<String, Object> = userService.getKakaoUserInfo(accessToken)
        println("###access_Token#### : $accessToken")
        println("###nickname#### : " + userInfo["nickname"])
        val nickname: String = userInfo["nickname"].toString()
        // println("###email#### : " + userInfo["email"])

        val signupRequest = SignupRequest(nickname, "", nickname, null)
        userService.signup(signupRequest)

        val loginRequest = LoginRequest(nickname, "")
        return userService.login(loginRequest)
    }

    @Authenticated
    @GetMapping("validate")
    fun validate(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("RefreshToken") refreshToken: String,
        @UserContext userEntity: UserEntity
    ): AuthToken {

        return userService.validate(userEntity)
    }
}
