package com.wafflestudio.team2.jisik2n.core.answer.database

import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<AnswerEntity, Long>, CustomAnswerRepository {
    fun findFirstByUserOrderByCreatedAt(user: UserEntity): AnswerEntity?
}

interface CustomAnswerRepository

class CustomAnswerRepositoryImpl : CustomAnswerRepository
