package com.wafflestudio.team2.jisik2n.core.user.service

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.common.Jisik2n409
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.*
import com.wafflestudio.team2.jisik2n.core.user.dto.*
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.service.UserAnswerInteractionService
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
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

    fun login(loginRequest: LoginRequest): LoginResponse

    fun getKakaoToken(code: String): String

    fun kakaoLogin(accessToken: String): LoginResponse

    fun validate(userEntity: UserEntity): AuthToken

    fun logout(token: TokenRequest): String

    fun getMyQuestions(userEntity: UserEntity): List<QuestionsOfMyQuestions>

    fun getMyAnswers(userEntity: UserEntity): List<AnswersOfMyAnswers>

    fun getMyLikeQuestions(userEntity: UserEntity): List<QuestionsOfMyQuestions>

    fun getMyAllProfile(userEntity: UserEntity): MyAllProfileResponse

    fun putAccount(userEntity: UserEntity, userRequest: UserRequest): UserResponse

    fun deleteAccount(userEntity: UserEntity): String

    fun regenerateToken(tokenRequest: TokenRequest): AuthToken
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val blacklistTokenRepository: BlacklistTokenRepository,
    private val authTokenService: AuthTokenService,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val userAnswerInteractionService: UserAnswerInteractionService,
    private val passwordEncoder: PasswordEncoder,
    private val s3Service: S3Service
) : UserService {

    override fun signup(signupRequest: SignupRequest): AuthToken {
        checkDuplicatedUid(signupRequest.uid)
        checkDuplicatedUsername(signupRequest.username)
        val encodedPassword = this.passwordEncoder.encode(signupRequest.password)
        val userEntity = UserEntity.signup(signupRequest, encodedPassword)
        userRepository.save(userEntity)

        val accessToken = authTokenService.generateAccessTokenByUid(signupRequest.uid)
        val refreshToken = authTokenService.generateRefreshTokenByUid(signupRequest.uid)

        val tokenEntity = TokenEntity.of(accessToken, refreshToken, signupRequest.uid)

        tokenRepository.save(tokenEntity)

        return AuthToken.of(tokenEntity)
    }

    override fun signupCheckDuplicatedUid(request: Map<String, String>) {
        val uid = request["uid"]!!
        checkDuplicatedUid(uid)
    }

    @Transactional
    override fun login(loginRequest: LoginRequest): LoginResponse {
        val userEntity = userRepository.findByUid(loginRequest.uid)

        if (userEntity == null || !userEntity.uid.equals(loginRequest.uid)) {
            throw Jisik2n404("해당 아이디로 가입한 유저가 없습니다.")
        }

        if (userEntity.isActive == false) {
            throw Jisik2n400("탈퇴한 회원의 아이디입니다.")
        }

        if (!this.passwordEncoder.matches(loginRequest.password, userEntity.password)) {
            throw Jisik2n401("비밀번호가 일치하지 않습니다.")
        }

        val accessToken = authTokenService.generateAccessTokenByUid(loginRequest.uid)

        val lastLogin = LocalDateTime.from(authTokenService.getCurrentIssuedAt(accessToken))
        userEntity.lastLogin = lastLogin

        val tokenEntity = tokenRepository.findByKeyUid(loginRequest.uid)!!
        tokenEntity.accessToken = accessToken

        if (authTokenService.verifyToken(tokenEntity.refreshToken) == false) {
            val refreshToken = authTokenService.generateRefreshTokenByUid(loginRequest.uid)
            tokenEntity.refreshToken = refreshToken
        }

        return LoginResponse.of(tokenEntity, userEntity.username)
    }

    override fun getKakaoToken(code: String): String {
        val url = URL("https://kauth.kakao.com/oauth/token")
        val urlConnection = url.openConnection() as HttpURLConnection

        var readline = "1"
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
            readline = br.readLine().toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return readline
    }

    @Transactional
    override fun kakaoLogin(accessToken: String): LoginResponse {

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
            userRepository.save(UserEntity(kakaoUsername, snsId, kakaoUsername, null, null, gender, null, null))

            val accessToken = authTokenService.generateAccessTokenByUid(kakaoUsername)
            val refreshToken = authTokenService.generateRefreshTokenByUid(kakaoUsername)

            val tokenEntity = TokenEntity.of(accessToken, refreshToken, kakaoUsername)

            tokenRepository.save(tokenEntity)

            return LoginResponse.of(tokenEntity, kakaoUsername)
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

            return LoginResponse.of(tokenEntity, kakaoUsername)
        }
    }

    override fun validate(userEntity: UserEntity): AuthToken {
        val uid = userRepository.findByIdOrNull(userEntity.id)?.uid ?: throw Jisik2n400("user가 존재하지 않습니다")
        val token = tokenRepository.findByKeyUid(uid) ?: throw Jisik2n400("token을 찾지 못했습니다")
        return AuthToken.of(token)
    }

    override fun logout(tokenRequest: TokenRequest): String {
        if (tokenRepository.findByAccessTokenAndRefreshToken(tokenRequest.accessToken, tokenRequest.refreshToken) != null) {
            val blacklistTokenEntity = BlacklistTokenEntity.of(tokenRequest)
            blacklistTokenRepository.save(blacklistTokenEntity)
            return "1"
        } else {
            throw Jisik2n400("token이 올바르지 않습니다")
        }
    }

    override fun getMyQuestions(userEntity: UserEntity): List<QuestionsOfMyQuestions> {
        return questionRepository.getQuestionsOfMyQuestions(userEntity.username)
    }

    override fun getMyAnswers(userEntity: UserEntity): List<AnswersOfMyAnswers> {
        return answerRepository.getAnswersOfMyAnswers(userEntity.username)
    }

    override fun getMyLikeQuestions(userEntity: UserEntity): List<QuestionsOfMyQuestions> {
        return questionRepository.getQuestionsOfMyLikeQuestions(userEntity.username)
    }
    override fun getMyAllProfile(userEntity: UserEntity): MyAllProfileResponse {
        val questions: List<QuestionsOfMyAllProfile> = questionRepository.getQuestionsOfMyAllProfile(userEntity.username)
        val answers: List<AnswersOfMyAllProfile> = answerRepository.getAnswersOfMyAllProfile(userEntity.username)
        return MyAllProfileResponse.of(userEntity, questions, answers)
    }

    @Transactional
    override fun putAccount(userEntity: UserEntity, userRequest: UserRequest): UserResponse {
        if (userRepository.findByUsername(userRequest.username) == null) {
            userEntity.username = userRequest.username
        } else {
            if (userEntity.username == userRequest.username) {
                userEntity.username = userRequest.username
            } else {
                throw Jisik2n409("해당 닉네임을 가진 유저가 있습니다.")
            }
        }

        userEntity.profileImage = userRequest.profileImage
        userEntity.isMale = userRequest.isMale
        return UserResponse(userEntity.username, userEntity.profileImage, userEntity.isMale)
    }

    @Transactional
    override fun deleteAccount(userEntity: UserEntity): String {
        userEntity.isActive = false
        return "탈퇴가 완료되었습니다"
    }

    override fun regenerateToken(tokenRequest: TokenRequest): AuthToken {
        return authTokenService.regenerateToken(tokenRequest)
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
