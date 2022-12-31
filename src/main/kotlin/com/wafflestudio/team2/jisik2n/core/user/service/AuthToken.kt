package com.wafflestudio.team2.jisik2n.core.user.service

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun of(accessToken: String, refreshToken: String):AuthToken{
            return AuthToken(accessToken, refreshToken)
        }
    }
}