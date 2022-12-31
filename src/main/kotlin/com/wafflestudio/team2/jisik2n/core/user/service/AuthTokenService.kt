package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
@EnableConfigurationProperties(AuthProperties::class)
class AuthTokenService(
    private val authProperties: AuthProperties,
    private val userRepository: UserRepository
) {

    private val tokenPrefix = "Bearer "
    private val signingKey = Keys.hmacShaKeyFor(authProperties.jwtSecret.toByteArray())

    fun generateAccessTokenByUid(uid: String): String {
        return generateTokenStringByUid("access", uid, authProperties.jwtAccessExpiration)
    }

    fun generateRefreshTokenByUid(uid: String): String {
        return generateTokenStringByUid("refresh", uid, authProperties.jwtAccessExpiration)
    }

    fun verifyToken(authToken: String): Boolean? {
        try {
            parse(authToken)
            println(parse(authToken))
        } catch (exception: Exception) {
            return false
        }
        return true
    }

    fun getCurrentUserId(authToken: String) : Long? {
        val uid = getCurrentUid(authToken)
        val userEntity = userRepository.findByUid(uid) ?: throw Jisik2n404("해당 아이디로 가입한 유저가 없습니다.")
        return userEntity.id

    }
    fun getCurrentUid(authToken: String) : String {

        return parse(authToken).body["uid"].toString()
    }
    fun getCurrentIssuedAt(authToken: String) : LocalDateTime {
        return parse(authToken).body.issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() // Date -> LocalDateTime
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

    private fun generateTokenStringByUid(accessOrRefresh: String, uid: String, jwtExpiration: Long):String{
        val claims : MutableMap<String, Any>
        val expiryDate: Date
        val now = System.currentTimeMillis()
        val nowDate = Date(now)

        if(accessOrRefresh == "access"){
            claims = Jwts.claims().setSubject("access")
            claims["uid"] = uid
            expiryDate = Date(nowDate.time+ Duration.ofSeconds(authProperties.jwtAccessExpiration).toMillis())
        } else {
            claims = Jwts.claims().setSubject("refresh")
            expiryDate = Date(nowDate.time+ Duration.ofSeconds(authProperties.jwtRefreshExpiration).toMillis())
        }


        return Jwts.builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setIssuer(authProperties.issuer).setIssuedAt(nowDate).setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256).compact()

    }
}