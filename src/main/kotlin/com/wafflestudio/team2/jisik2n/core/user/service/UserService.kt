package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.user.api.request.LoginRequest
import com.wafflestudio.team2.jisik2n.core.user.api.request.SignupRequest
import com.wafflestudio.team2.jisik2n.core.user.database.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val authTokenService: AuthTokenService,
    private val passwordEncoder: PasswordEncoder
) {

    fun signup(request: SignupRequest): AuthToken {

        val encodedPassword = this.passwordEncoder.encode(request.password)
        val userEntity = UserEntity.of(request, encodedPassword)
        userRepository.save(userEntity)

        val accessToken = authTokenService.generateAccessTokenByUid(request.uid)
        val refreshToken = authTokenService.generateRefreshTokenByUid(request.uid)
        val tokenEntity = TokenEntity.of(accessToken, refreshToken, request.uid)

        tokenRepository.save(tokenEntity)

        return AuthToken.of(accessToken, refreshToken)
    }

    @Transactional
    fun login(request: LoginRequest): AuthToken {
        val userEntity = userRepository.findByUid(request.uid) ?: throw Jisik2n404("해당 아이디로 가입한 유저가 없습니다.")

        if (!this.passwordEncoder.matches(request.password, userEntity.password)) {
            throw Jisik2n401("비밀번호가 일치하지 않습니다.")
        }

        val accessToken = authTokenService.generateAccessTokenByUid(request.uid)
        val lastLogin = LocalDateTime.from(authTokenService.getCurrentIssuedAt(accessToken))
        userEntity.lastLogin = lastLogin

        val tokenEntity = tokenRepository.findByKeyUid(request.uid)!!
        tokenEntity.accessToken = accessToken

        return AuthToken.of(tokenEntity.accessToken, tokenEntity.refreshToken)
    }

    fun validate(userId: Long): AuthToken {
        val uid = userRepository.findByIdOrNull(userId)?.uid ?: throw Jisik2n400("user가 존재하지 않습니다")

        val token = tokenRepository.findByKeyUid(uid) ?: throw Jisik2n400("token을 찾지 못했습니다")
        return AuthToken.of(token.accessToken, token.refreshToken)
    }
}
