package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.core.user.api.request.SignupRequest
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

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


}