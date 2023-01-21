package com.wafflestudio.team2.jisik2n.core.answer.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.core.question.database.QQuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.AnswersOfMyAllProfile
import com.wafflestudio.team2.jisik2n.core.user.dto.AnswersOfMyAnswers
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<AnswerEntity, Long>, CustomAnswerRepository {
    fun findFirstByUserOrderByCreatedAt(user: UserEntity): AnswerEntity?

    fun findAllByUser(user: UserEntity): List<AnswerEntity>
}

interface CustomAnswerRepository {
    fun getAnswersOfMyAnswers(username: String): List<AnswersOfMyAnswers>

    fun getAnswersOfMyAllProfile(username: String): List<AnswersOfMyAllProfile>
}

@Component
class CustomAnswerRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomAnswerRepository {
    override fun getAnswersOfMyAnswers(username: String): List<AnswersOfMyAnswers> {
        val answerEntity: QAnswerEntity = QAnswerEntity.answerEntity
        val questionEntity: QQuestionEntity = QQuestionEntity.questionEntity
        return queryFactory.select(
            Projections.constructor(
                AnswersOfMyAnswers::class.java,
                questionEntity.id,
                questionEntity.title,
                answerEntity.createdAt
            )
        )
            .from(answerEntity).where(answerEntity.user.username.eq(username))
            .leftJoin(questionEntity).on(answerEntity.question.eq(questionEntity)).fetchJoin()
            .orderBy(answerEntity.createdAt.asc())
            .fetch()
    }

    override fun getAnswersOfMyAllProfile(username: String): List<AnswersOfMyAllProfile> {
        val answerEntity: QAnswerEntity = QAnswerEntity.answerEntity
        return queryFactory.select(
            Projections.constructor(
                AnswersOfMyAllProfile::class.java,
                answerEntity.id,
                answerEntity.content,
                answerEntity.createdAt,
                answerEntity.selected,
                answerEntity.selectedAt
            )
        )
            .from(answerEntity).where(answerEntity.user.username.eq(username)).fetch()
    }
}
