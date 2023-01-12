package com.wafflestudio.team2.jisik2n.core.question.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.core.user.dto.Questions
import org.springframework.stereotype.Component

@Component
class QuestionDslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun getMyQuestions(username: String): List<Questions> {
        val questionEntity: QQuestionEntity = QQuestionEntity.questionEntity

        return queryFactory.select(
            Projections.constructor(Questions::class.java, questionEntity.id, questionEntity.title, questionEntity.content)
        )
            .from(questionEntity).where(questionEntity.user.username.eq(username)).fetch()
    }
}
