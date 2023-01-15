package com.wafflestudio.team2.jisik2n.core.answer.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.AnswersOfMyAllProfile
import com.wafflestudio.team2.jisik2n.core.user.dto.AnswersOfMyAnswers
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.QUserAnswerInteractionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<AnswerEntity, Long>, CustomAnswerRepository {
    fun findFirstByUserOrderByCreatedAt(user: UserEntity): AnswerEntity?
}

interface CustomAnswerRepository {
    fun getAnswersOfMyAnswers(username: String): List<AnswersOfMyAnswers>

    fun getAnswersOfMyAllProfile(username: String): List<AnswersOfMyAllProfile>

    fun getAnswersOfMyAgreeAnswers(userEntity: UserEntity): List<AnswersOfMyAnswers>
}

@Component
class CustomAnswerRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomAnswerRepository {
    override fun getAnswersOfMyAnswers(username: String): List<AnswersOfMyAnswers> {
        val answerEntity: QAnswerEntity = QAnswerEntity.answerEntity
        return queryFactory.select(
            Projections.constructor(
                AnswersOfMyAnswers::class.java,
                answerEntity.id,
                answerEntity.content
            )
        )
            .from(answerEntity).where(answerEntity.user.username.eq(username)).fetch()
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

    override fun getAnswersOfMyAgreeAnswers(userEntity: UserEntity): List<AnswersOfMyAnswers> {
        val answerEntity: QAnswerEntity = QAnswerEntity.answerEntity
        val userAnswerInteractionEntity: QUserAnswerInteractionEntity = QUserAnswerInteractionEntity.userAnswerInteractionEntity
        return queryFactory.select(
            Projections.constructor(
                AnswersOfMyAnswers::class.java,
                answerEntity.id,
                answerEntity.content
            )
        )
            .from(answerEntity).join(answerEntity.userAnswerInteractions, userAnswerInteractionEntity).where(userAnswerInteractionEntity.user.username.eq(userEntity.username))
            .fetch()
    }
}
