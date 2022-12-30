package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
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

    private val authProperties: AuthProperties
) {

    private val tokenPrefix = "Bearer "
    private val signingKey = Keys.hmacShaKeyFor(authProperties.jwtSecret.toByteArray())

    fun generateTokenByUid(uid: String): AuthToken {
        val claims: MutableMap<String, Any> = Jwts.claims().setSubject("access")

        claims["uid"] = uid

        val now = System.currentTimeMillis()
        val nowDate = Date(now)
        val expiryDate: Date = Date(nowDate.time+ Duration.ofSeconds(authProperties.jwtExpiration).toMillis())
        println(expiryDate)
        val resultToken = Jwts.builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(claims)
            .setIssuer(authProperties.issuer).setIssuedAt(nowDate).setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256).compact()

        return AuthToken(resultToken)
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
}