package com.wafflestudio.team2.jisik2n.core.user.database

import org.springframework.data.jpa.repository.JpaRepository

interface BlacklistTokenRepository : JpaRepository<BlacklistTokenEntity, Long> {

    fun save(blacklistTokenEntity: BlacklistTokenEntity): BlacklistTokenEntity

    fun findByAccessToken(accessToken: String): BlacklistTokenEntity?
}
