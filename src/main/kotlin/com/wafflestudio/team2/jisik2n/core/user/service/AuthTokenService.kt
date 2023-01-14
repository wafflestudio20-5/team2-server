package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.user.database.TokenRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import com.wafflestudio.team2.jisik2n.core.user.dto.TokenRequest
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.transaction.Transactional

interface AuthTokenService {
    fun generateAccessTokenByUid(uid: String): String

    fun generateRefreshTokenByUid(uid: String): String

    fun regenerateToken(token: TokenRequest): AuthToken

    fun verifyToken(authToken: String): Boolean?

    fun getCurrentUserId(authToken: String): Long?

    fun getCurrentUid(authToken: String): String

    fun getCurrentIssuedAt(authToken: String): LocalDateTime

    fun getCurrentExpiration(authToken: String): LocalDateTime
}

@Service
@EnableConfigurationProperties(AuthProperties::class)
class AuthTokenServiceImpl(
    private val authProperties: AuthProperties,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : AuthTokenService {

    private val tokenPrefix = "Bearer "
    private val signingKey = Keys.hmacShaKeyFor(authProperties.jwtSecret.toByteArray())

    override fun generateAccessTokenByUid(uid: String): String {
        return generateTokenStringByUid("access", uid)
    }

    override fun generateRefreshTokenByUid(uid: String): String {
        return generateTokenStringByUid("refresh", uid)
    }

    @Transactional
    override fun regenerateToken(token: TokenRequest): AuthToken {
        if (verifyToken(token.refreshToken) != true) {
            throw Jisik2n401("로그인을 다시 해야 합니다")
        } else {
            val tokenEntity = tokenRepository.findByRefreshToken(token.refreshToken)!!
            val newAccessToken = generateTokenStringByUid("access", tokenEntity.keyUid)
            tokenEntity.accessToken = newAccessToken

            return AuthToken(newAccessToken, token.refreshToken)
        }
    }

    override fun verifyToken(authToken: String): Boolean? {
        try {
            parse(authToken)
        } catch (exception: Exception) {
            return false
        }
        return true
    }

    override fun getCurrentUserId(authToken: String): Long? {
        val uid = getCurrentUid(authToken)
        val userEntity = userRepository.findByUid(uid) ?: throw Jisik2n404("해당 아이디로 가입한 유저가 없습니다.")
        return userEntity.id
    }

    override fun getCurrentUid(authToken: String): String {
        return parse(authToken).body["uid"].toString()
    }

    override fun getCurrentIssuedAt(authToken: String): LocalDateTime {
        return parse(authToken).body.issuedAt.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDateTime() // Date -> LocalDateTime
    }

    override fun getCurrentExpiration(authToken: String): LocalDateTime {
        return parse(authToken).body.expiration.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDateTime() // Date -> LocalDateTime
    }

    private fun parse(authToken: String): Jws<Claims> {
        val prefixRemoved = authToken.replace(tokenPrefix, "").trim { it <= ' ' }
        try {
            return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(prefixRemoved)
        } catch (e: SignatureException) {
            throw Jisik2n404("인증이 되지 않았습니다")
        } catch (e: ExpiredJwtException) {
            throw Jisik2n401("인증이 되지 않았습니다")
        }
    }

    private fun generateTokenStringByUid(accessOrRefresh: String, uid: String): String {
        val claims: MutableMap<String, Any>
        val expiryDate: Date
        val now = System.currentTimeMillis()
        val nowDate = Date(now)

        if (accessOrRefresh == "access") {
            claims = Jwts.claims().setSubject("access")
            claims["uid"] = uid
            expiryDate = Date(nowDate.time + Duration.ofSeconds(authProperties.jwtAccessExpiration).toMillis())
        } else {
            claims = Jwts.claims().setSubject("refresh")
            expiryDate = Date(nowDate.time + Duration.ofSeconds(authProperties.jwtRefreshExpiration).toMillis())
        }

        return Jwts.builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setIssuer(authProperties.issuer).setIssuedAt(nowDate).setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256).compact()
    }
}
