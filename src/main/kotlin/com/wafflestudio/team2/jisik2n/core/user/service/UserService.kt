package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.user.api.request.LoginRequest
import com.wafflestudio.team2.jisik2n.core.user.api.request.SignupRequest
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authTokenService: AuthTokenService,
    private val passwordEncoder: PasswordEncoder
) {

    fun signup(request: SignupRequest):AuthToken {
        if(request.password != request.password2) {
            throw Jisik2n400("비밀번호가 일치하지 않습니다")
        }

        val encodedPassword = this.passwordEncoder.encode(request.password)
        val userEntity = UserEntity.of(request,encodedPassword)
        userRepository.save(userEntity)

        return authTokenService.generateTokenByUid(request.uid)
    }

    @Transactional
    fun login(request: LoginRequest): AuthToken {
        val userEntity = userRepository.findByUid(request.uid) ?: throw Jisik2n404("해당 아이디로 가입한 유저가 없습니다.")

        if(!this.passwordEncoder.matches(request.password, userEntity.password)) {
            throw Jisik2n401("비밀번호가 일치하지 않습니다.")
        }

        val token = authTokenService.generateTokenByUid(request.uid)
        val lastLogin = LocalDateTime.from(authTokenService.getCurrentIssuedAt(token.accessToken))
        userEntity.lastLogin = lastLogin
        return token
    }
}