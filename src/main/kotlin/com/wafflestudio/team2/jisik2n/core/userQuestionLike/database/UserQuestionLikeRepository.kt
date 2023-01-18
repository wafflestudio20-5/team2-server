package com.wafflestudio.team2.jisik2n.core.userQuestionLike.database

import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserQuestionLikeRepository : JpaRepository<UserQuestionLikeEntity, Long> {
    fun findByQuestionAndUser(question: QuestionEntity, user: UserEntity): UserQuestionLikeEntity?
}
