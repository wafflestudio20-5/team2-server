package com.wafflestudio.team2.jisik2n.core.user.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.*
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
    fun login(@RequestBody loginRequest: LoginRequest): LoginResponse {
        return userService.login(loginRequest)
    }

    @GetMapping("getKakaoToken")
    fun getKakaoToken(@RequestParam(value = "code", required = false) code: String): String {
        return userService.getKakaoToken(code)
    }

    @GetMapping("kakaoLogin")
    fun kakaoLogin(@RequestParam(value = "accessToken", required = false) accessToken: String): LoginResponse {

        return userService.kakaoLogin(accessToken)
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

    @PostMapping("logout")
    fun logout(@RequestBody tokenRequest: TokenRequest): String {
        return userService.logout(tokenRequest)
    }

    @Authenticated
    @GetMapping("myQuestions")
    fun getMyQuestions(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("RefreshToken") refreshToken: String,
        @UserContext userEntity: UserEntity
    ): MyQuestionsResponse {
        return userService.getMyQuestions(userEntity)
    }

    @Authenticated
    @GetMapping("myAnswers")
    fun getMyAnswers(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("RefreshToken") refreshToken: String,
        @UserContext userEntity: UserEntity
    ): MyAnswersResponse {
        return userService.getMyAnswers(userEntity)
    }

    fun deleteAccount() {
        // 탈퇴할 때, 질문이랑 대답 조회해서 거기에 있는 사용자 null로 바꿔버리기
    }

    fun putAccount() {
        println("기대된다 기대돼")
    }

    fun regenerateAccessToken() {}
}
