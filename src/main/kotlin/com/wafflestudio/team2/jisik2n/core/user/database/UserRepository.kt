package com.wafflestudio.team2.jisik2n.core.user.database

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<UserEntity,Long> {

    fun save(userEntity: UserEntity): UserEntity

    fun findByUid(uid: String): UserEntity?
}