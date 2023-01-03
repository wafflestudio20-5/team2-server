package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.common.Jisik2n409
import com.wafflestudio.team2.jisik2n.core.user.database.*
import com.wafflestudio.team2.jisik2n.core.user.dto.LoginRequest
import com.wafflestudio.team2.jisik2n.core.user.dto.SignupRequest
import com.wafflestudio.team2.jisik2n.core.user.dto.TokenRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import javax.transaction.Transactional

interface UserService {
    fun signup(signupRequest: SignupRequest): AuthToken

    fun login(loginRequest: LoginRequest): AuthToken

    fun getKaKaoToken(code: String): String

    fun logout(token: TokenRequest): String
    fun validate(userEntity: UserEntity): AuthToken
}
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val blacklistTokenRepository: BlacklistTokenRepository,
    private val authTokenService: AuthTokenService,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun signup(request: SignupRequest): AuthToken {
        checkDuplicatedUid(request.uid)
        checkDuplicatedUsername(request.username)
        val encodedPassword = this.passwordEncoder.encode(request.password)
        val userEntity = UserEntity.of(request, encodedPassword)
        userRepository.save(userEntity)

        val accessToken = authTokenService.generateAccessTokenByUid(request.uid)
        val refreshToken = authTokenService.generateRefreshTokenByUid(request.uid)

        val tokenEntity = TokenEntity.of(accessToken, refreshToken, request.uid)

        tokenRepository.save(tokenEntity)

        return AuthToken.of(tokenEntity)
    }

    @Transactional
    override fun login(request: LoginRequest): AuthToken {
        val userEntity = userRepository.findByUid(request.uid) ?: throw Jisik2n404("해당 아이디로 가입한 유저가 없습니다.")

        if (!this.passwordEncoder.matches(request.password, userEntity.password)) {
            throw Jisik2n401("비밀번호가 일치하지 않습니다.")
        }

        val accessToken = authTokenService.generateAccessTokenByUid(request.uid)

        val lastLogin = LocalDateTime.from(authTokenService.getCurrentIssuedAt(accessToken))
        userEntity.lastLogin = lastLogin

        val tokenEntity = tokenRepository.findByKeyUid(request.uid)!!
        tokenEntity.accessToken = accessToken

        if (authTokenService.getCurrentExpiration(tokenEntity.refreshToken) < LocalDateTime.now()) {
            val refreshToken = authTokenService.generateRefreshTokenByUid(request.uid)
            tokenEntity.refreshToken = refreshToken
        }

        return AuthToken.of(tokenEntity)
    }

    override fun getKaKaoToken(code: String): String {
        val url: URL = URL("https://kauth.kakao.com/oauth/token")
        val urlConnection = url.openConnection() as HttpURLConnection
        val token: String = ""

        try {
            urlConnection.requestMethod = "POST"
            urlConnection.doOutput = true

            val bw = BufferedWriter(OutputStreamWriter(urlConnection.outputStream))
            val sb: StringBuilder = StringBuilder()
            sb.append("grant_type=authorization_code")
            sb.append("&client_id=1f902696f45d3f80db2635c82134a150")
            sb.append("&redirect_url=http://43.200.186.212/api/user/users")
            sb.append("&code=$code")

            bw.write(sb.toString())
            bw.flush()

            val responseCode: Int = urlConnection.responseCode
            println(responseCode)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return "1"
    }

    override fun logout(request: TokenRequest): String {
        if (tokenRepository.findByAccessTokenAndRefreshToken(request.accessToken, request.refreshToken) != null) {
            val blacklistTokenEntity = BlacklistTokenEntity.of(request)
            blacklistTokenRepository.save(blacklistTokenEntity)

            return "1"
        } else {
            throw Jisik2n400("token이 올바르지 않습니다")
        }
    }

    override fun validate(userEntity: UserEntity): AuthToken {
        val uid = userRepository.findByIdOrNull(userEntity.id)?.uid ?: throw Jisik2n400("user가 존재하지 않습니다")
        val token = tokenRepository.findByKeyUid(uid) ?: throw Jisik2n400("token을 찾지 못했습니다")
        return AuthToken.of(token)
    }

    private fun checkDuplicatedUid(uid: String) {
        userRepository.findByUid(uid)?.let { throw Jisik2n409("이미 가입한 아이디입니다.") }
    }

    private fun checkDuplicatedUsername(username: String) {
        userRepository.findByUsername(username)?.let { throw Jisik2n409("이미 생성된 별명입니다.") }
    }
}
