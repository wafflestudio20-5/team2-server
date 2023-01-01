package com.wafflestudio.team2.jisik2n.core.user.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {

    fun save(userEntity: UserEntity): UserEntity

    fun findByUid(uid: String): UserEntity?

    fun findByUsername(uid: String): UserEntity?
}
