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
        @UserContext userEntity: UserEntity,
        @RequestParam(required = false, defaultValue = "20") amount: Long,
        @RequestParam(required = false, defaultValue = "0") pageNum: Long,
    ): List<QuestionsOfMyQuestions> {
        return userService.getMyQuestions(userEntity, amount, pageNum)
    }

    @Authenticated
    @GetMapping("myAnswers")
    fun getMyAnswers(
        @UserContext userEntity: UserEntity,
        @RequestParam(required = false, defaultValue = "20") amount: Long,
        @RequestParam(required = false, defaultValue = "0") pageNum: Long,
    ): List<AnswersOfMyAnswers> {
        return userService.getMyAnswers(userEntity, amount, pageNum)
    }

    @Authenticated
    @GetMapping("myLikeQuestions")
    fun getMyLikeQuestions(
        @UserContext userEntity: UserEntity
    ): List<QuestionsOfMyQuestions> {
        return userService.getMyLikeQuestions(userEntity)
    }

    @Authenticated
    @GetMapping("myAllProfile")
    fun getMyAllProfile(
        @UserContext userEntity: UserEntity
    ): MyAllProfileResponse {
        return userService.getMyAllProfile(userEntity)
    }

    @Authenticated
    @PutMapping("putAccount")
    fun putAccount(
        @UserContext userEntity: UserEntity,
        @RequestBody userRequest: UserRequest
    ): UserResponse {
        return userService.putAccount(userEntity, userRequest)
    }

    @Authenticated
    @DeleteMapping("deleteAccount")
    fun deleteAccount(
        @UserContext userEntity: UserEntity
    ): String {
        return userService.deleteAccount(userEntity)
    }

    @Authenticated
    @DeleteMapping("deleteAccountWithPassword")
    fun deleteAccountWithPassword(
        @UserContext userEntity: UserEntity,
        @RequestBody request: Map<String, String>
    ): String {
        return userService.deleteAccountWithPassword(userEntity, request)
    }

    @PostMapping("regenerateToken")
    fun regenerateToken(@RequestBody tokenRequest: TokenRequest): AuthToken {
        return userService.regenerateToken(tokenRequest)
    }
}
