package com.wafflestudio.team2.jisik2n.core.question.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.common.SEARCH_ORDER
import com.wafflestudio.team2.jisik2n.core.answer.database.QAnswerEntity.answerEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QQuestionEntity.questionEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.QSearchResponse
import com.wafflestudio.team2.jisik2n.core.question.dto.SearchResponse
import com.wafflestudio.team2.jisik2n.core.user.dto.QuestionsOfMyAllProfile
import com.wafflestudio.team2.jisik2n.core.user.dto.QuestionsOfMyQuestions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

interface QuestionRepository : JpaRepository<QuestionEntity, Long>, CustomQuestionRepository

interface CustomQuestionRepository {
    fun getQuestionsOfMyQuestions(username: String): List<QuestionsOfMyQuestions>
    fun getQuestionsOfMyAllProfile(username: String): List<QuestionsOfMyAllProfile>
    fun searchAndOrderPagination(
        order: SEARCH_ORDER,
        isClosed: Boolean? = null,
        keyword: String,
        amount: Long,
        pageNum: Long
    ): MutableList<SearchResponse>
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

    override fun searchAndOrderPagination(order: SEARCH_ORDER, isClosed: Boolean?, keyword: String, amount: Long, pageNum: Long): MutableList<SearchResponse> {
        val searchResponses = queryFactory.select(
            QSearchResponse(
                questionEntity.id,
                questionEntity.title,
                questionEntity.content,
                answerEntity.content,
                questionEntity.answerCount,
                questionEntity.likeCount,
            )
        ).from(questionEntity)
            .join(questionEntity.answers, answerEntity).fetchJoin()
            .where(
                questionEntity.title.contains(keyword) // Search for keywords
                    .or((questionEntity.content.contains(keyword)))
                    .or((answerEntity.content.contains(keyword))),
                when (isClosed) { // Filter with close
                    true -> questionEntity.close.eq(true)
                    false -> questionEntity.close.eq(false)
                    null -> null
                }
            )
            .orderBy(
                when (order) { // Order by date or like
                    SEARCH_ORDER.DATE -> questionEntity.createdAt.asc()
                    SEARCH_ORDER.LIKE -> questionEntity.likeCount.asc()
                }
            )
            .offset(pageNum) // pagination
            .limit(amount)
            .fetch()

        return searchResponses
    }
}
