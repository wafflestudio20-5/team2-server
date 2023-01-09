package com.wafflestudio.team2.jisik2n.core.user.database

import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository : JpaRepository<TokenEntity, Long> {

    fun save(TokenEntity: TokenEntity): TokenEntity

    fun findByAccessToken(accessToken: String): TokenEntity?

    fun findByKeyUid(keyUid: String): TokenEntity?

    fun findByAccessTokenAndRefreshToken(accessToken: String, refreshToken: String): TokenEntity?
}
