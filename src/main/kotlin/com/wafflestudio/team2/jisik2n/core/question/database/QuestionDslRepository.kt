package com.wafflestudio.team2.jisik2n.core.question.database

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class QuestionDslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun getQuestionsByUsername(username: String) {
        val questionEntity: QQuestionEntity = QQuestionEntity.questionEntity

        queryFactory.select(questionEntity.title, questionEntity.content, questionEntity.user.username)
            .from(questionEntity).where(questionEntity.user.username.eq(username))
    }
}
