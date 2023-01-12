package com.wafflestudio.team2.jisik2n.core.answer.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.core.user.dto.Answers
import org.springframework.stereotype.Component

@Component
class AnswerDslRepository(
    private val queryFactory: JPAQueryFactory
) {

    fun getMyAnswers(username: String): List<Answers> {
        val answerEntity: QAnswerEntity = QAnswerEntity.answerEntity
        return queryFactory.select(Projections.constructor(Answers::class.java, answerEntity.id, answerEntity.content))
            .from(answerEntity).where(answerEntity.user.username.eq(username)).fetch()
    }
}
