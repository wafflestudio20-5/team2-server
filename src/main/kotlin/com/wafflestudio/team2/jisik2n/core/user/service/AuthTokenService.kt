package com.wafflestudio.team2.jisik2n.core.user.service

import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.time.Duration
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
}