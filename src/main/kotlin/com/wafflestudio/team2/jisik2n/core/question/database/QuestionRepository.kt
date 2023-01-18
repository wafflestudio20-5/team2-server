package com.wafflestudio.team2.jisik2n.core.question.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.core.user.dto.QuestionsOfMyAllProfile
import com.wafflestudio.team2.jisik2n.core.user.dto.QuestionsOfMyQuestions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity

interface QuestionRepository : JpaRepository<QuestionEntity, Long>, CustomQuestionRepository {
    fun findAllByUser(user: UserEntity): List<QuestionEntity>
}

interface CustomQuestionRepository {
    fun getQuestionsOfMyQuestions(username: String): List<QuestionsOfMyQuestions>

    fun getQuestionsOfMyAllProfile(username: String): List<QuestionsOfMyAllProfile>
}

@Component
class QuestionRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomQuestionRepository {
    override fun getQuestionsOfMyQuestions(username: String): List<QuestionsOfMyQuestions> {
        val questionEntity: QQuestionEntity = QQuestionEntity.questionEntity

        return queryFactory.select(
            Projections.constructor(
                QuestionsOfMyQuestions::class.java,
                questionEntity.id,
                questionEntity.title,
                questionEntity.content
            )
        )
            .from(questionEntity).where(questionEntity.user.username.eq(username)).fetch()
    }

    override fun getQuestionsOfMyAllProfile(username: String): List<QuestionsOfMyAllProfile> {
        val questionEntity: QQuestionEntity = QQuestionEntity.questionEntity

        return queryFactory.select(
            Projections.constructor(
                QuestionsOfMyAllProfile::class.java,
                questionEntity.id,
                questionEntity.title,
                questionEntity.content,
                questionEntity.answers.size(),
                questionEntity.createdAt,
                questionEntity.close,
                questionEntity.closedAt
            )
        )
            .from(questionEntity).where(questionEntity.user.username.eq(username)).fetch()
    }
}
