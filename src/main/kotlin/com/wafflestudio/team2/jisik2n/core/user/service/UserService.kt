package com.wafflestudio.team2.jisik2n.core.user.service

import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import javax.transaction.Transactional

interface UserService {
    fun signup(signupRequest: SignupRequest): AuthToken

    fun signupCheckDuplicatedUid(request: Map<String, String>)

    fun login(loginRequest: LoginRequest): AuthToken

    fun getKakaoToken(code: String): String

    fun kakaoLogin(accessToken: String): AuthToken

    fun logout(token: TokenRequest): String
    fun validate(userEntity: UserEntity): AuthToken

    fun deleteKakaoAccount(userEntity: UserEntity): String
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
        val userEntity = UserEntity.signup(request, encodedPassword)
        userRepository.save(userEntity)

        val accessToken = authTokenService.generateAccessTokenByUid(request.uid)
        val refreshToken = authTokenService.generateRefreshTokenByUid(request.uid)

        val tokenEntity = TokenEntity.of(accessToken, refreshToken, request.uid)

        tokenRepository.save(tokenEntity)

        return AuthToken.of(tokenEntity)
    }

    override fun signupCheckDuplicatedUid(request: Map<String, String>) {
        val uid = request["uid"]!!
        checkDuplicatedUid(uid)
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

    override fun getKakaoToken(code: String): String {
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
            sb.append("&redirect_url=https://naver.com")
            sb.append("&code=$code")

            bw.write(sb.toString())
            bw.flush()

            val responseCode: Int = urlConnection.responseCode
            println(responseCode)

            val br = BufferedReader(InputStreamReader(urlConnection.inputStream))
            println(br.readLine())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return "1"
    }

    @Transactional
    override fun kakaoLogin(accessToken: String): AuthToken {

        val userInfo: HashMap<String, String> = getKakaoUserInfo(accessToken)

        val username: String = userInfo["username"]!!
        val snsId = userInfo["snsId"]
        val gender: Boolean? =
            if (userInfo["gender"] == "true") {
                true
            } else if (userInfo["gender"] == "false") {
                false
            } else {
                null
            }

        val kakaoUsername = "kakao-$username"
        if (userRepository.findByUsername(kakaoUsername) == null) {
            userRepository.save(UserEntity(kakaoUsername, snsId, kakaoUsername, null, null, gender, null))

            val accessToken = authTokenService.generateAccessTokenByUid(kakaoUsername)
            val refreshToken = authTokenService.generateRefreshTokenByUid(kakaoUsername)

            val tokenEntity = TokenEntity.of(accessToken, refreshToken, kakaoUsername)

            tokenRepository.save(tokenEntity)

            return AuthToken.of(tokenEntity)
        } else {
            val userEntity = userRepository.findByUsername(kakaoUsername)!!
            val accessToken = authTokenService.generateAccessTokenByUid(kakaoUsername)

            val lastLogin = LocalDateTime.from(authTokenService.getCurrentIssuedAt(accessToken))
            userEntity.lastLogin = lastLogin

            val tokenEntity = tokenRepository.findByKeyUid(kakaoUsername)!!
            tokenEntity.accessToken = accessToken

            if (authTokenService.getCurrentExpiration(tokenEntity.refreshToken) < LocalDateTime.now()) {
                val refreshToken = authTokenService.generateRefreshTokenByUid(kakaoUsername)
                tokenEntity.refreshToken = refreshToken
            }

            return AuthToken.of(tokenEntity)
        }
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

    @Transactional
    override fun deleteKakaoAccount(userEntity: UserEntity): String {
        println(userEntity.uid)
        userRepository.deleteByUid(userEntity.uid)
        return "삭제 완료"
    }

    private fun checkDuplicatedUid(uid: String) {
        userRepository.findByUid(uid)?.let { throw Jisik2n409("이미 가입한 아이디입니다.") }
    }

    private fun checkDuplicatedUsername(username: String) {
        userRepository.findByUsername(username)?.let { throw Jisik2n409("이미 생성된 별명입니다.") }
    }

    private fun getKakaoUserInfo(accessToken: String): HashMap<String, String> {
        val userInfo = HashMap<String, String>()
        val reqURL = "https://kapi.kakao.com/v2/user/me"

        try {
            val url = URL(reqURL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $accessToken")

            val responseCode: Int = conn.responseCode
            println("responseCode: $responseCode")

            val br = BufferedReader(InputStreamReader(conn.inputStream))

            var line: String?
            var result: String? = ""

            while (br.readLine().also { line = it } != null) {
                result += line
            }

            val responseBody: JsonObject = JsonParser.parseString(result).asJsonObject
            val kakaoAccount: JsonObject = responseBody.asJsonObject.get("kakao_account").asJsonObject
            println("kakao account: $kakaoAccount")

            val nickname = kakaoAccount.asJsonObject.get("profile").asJsonObject["nickname"]
            userInfo["username"] = nickname.asString

            val email = kakaoAccount.asJsonObject.get("email")
            if (email != null) {
                userInfo["snsId"] = email.asString
            } else {
                userInfo["snsId"] = ""
            }

            val gender = kakaoAccount.asJsonObject.get("gender")
            if (gender != null) {
                if (gender.asString == "male") {
                    userInfo["gender"] = "true"
                } else {
                    userInfo["gender"] = "false"
                }
            } else {
                userInfo["gender"] = ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return userInfo
    }
}
