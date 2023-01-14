package com.wafflestudio.team2.jisik2n.core.user.dto

import com.wafflestudio.team2.jisik2n.core.user.database.TokenEntity

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String
) {
    companion object {
        fun of(token: TokenEntity, username: String): LoginResponse {
            return LoginResponse(token.accessToken, token.refreshToken, username)
        }
    }
}
