package com.wafflestudio.team2.jisik2n.core.user.service

import com.wafflestudio.team2.jisik2n.core.user.database.TokenEntity

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(token: TokenEntity): AuthToken {
            return AuthToken(token.accessToken, token.refreshToken)
        }
    }
}
