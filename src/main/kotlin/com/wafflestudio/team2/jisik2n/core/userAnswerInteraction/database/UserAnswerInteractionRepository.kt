package com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database

import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserAnswerInteractionRepository : JpaRepository<UserAnswerInteractionEntity, Long> {
    fun findByUserAndAnswer(user: UserEntity, answer: AnswerEntity): UserAnswerInteractionEntity?
    fun deleteByAnswer(answer: AnswerEntity)
}
