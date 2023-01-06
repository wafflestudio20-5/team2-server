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

    @GetMapping("getKakaoToken")
    fun getKakaoToken(@RequestParam(value = "code", required = false) code: String): String {
        return userService.getKakaoToken(code)
    }

    @GetMapping("kakaoLogin")
    fun kakaoLogin(@RequestParam(value = "accessToken", required = false) accessToken: String): AuthToken {

        val userInfo: HashMap<String, String> = userService.getKakaoUserInfo(accessToken)

        return userService.kakaoLogin(userInfo)
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

//    @Authenticated
//    @DeleteMapping("deleteKakaoAccount")
//    fun deleteKakaoAccount(
//        @RequestHeader("Authorization") accessToken: String,
//        @RequestHeader("RefreshToken") refreshToken: String,
//        @UserContext userEntity: UserEntity,
//    ): String {
//        return userService.deleteKakaoAccount(userEntity)
//    }
}
