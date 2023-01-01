package com.wafflestudio.team2.jisik2n.core.user.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity(name = "token")
class TokenEntity(

    @Column
    var accessToken: String,

    @Column(nullable = false)
    var refreshToken: String,

    @Column(nullable = false)
    val keyUid: String

) : BaseTimeEntity() {

    companion object {
        fun of(accessToken: String, refreshToken: String, uid: String): TokenEntity {
            return TokenEntity(accessToken, refreshToken, uid)
        }
    }
}
