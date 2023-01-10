package com.wafflestudio.team2.jisik2n.core.user.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.TokenRequest
import javax.persistence.Column
import javax.persistence.Entity

@Entity(name = "blacklistToken")
class BlacklistTokenEntity(

    @Column
    val accessToken: String,

    @Column
    val refreshToken: String

) : BaseTimeEntity() {

    companion object {
        fun of(tokenRequest: TokenRequest): BlacklistTokenEntity {
            return BlacklistTokenEntity(tokenRequest.accessToken, tokenRequest.refreshToken)
        }
    }
}
