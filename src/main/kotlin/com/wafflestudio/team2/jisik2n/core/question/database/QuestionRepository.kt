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
    ): List<SearchResponse>
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

    /**
     * More Efficient Version, but inaccurate pagination
     *      it only guarantees that number of dto is less or equal to amount
     *      sometimes it returns duplicated question
     */
    override fun searchAndOrderPagination(order: SEARCH_ORDER, isClosed: Boolean?, keyword: String, amount: Long, pageNum: Long): List<SearchResponse> {
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
            .leftJoin(questionEntity.answers, answerEntity)
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
                    SEARCH_ORDER.DATE -> questionEntity.createdAt.desc()
                    SEARCH_ORDER.LIKE -> questionEntity.likeCount.desc()
                    SEARCH_ORDER.ANSWER -> questionEntity.answerCount.desc()
                },
                questionEntity.id.desc(),
                answerEntity.id.asc(),
            )
            .offset(pageNum * amount) // pagination
            .limit(amount)
            .fetch()

        return searchResponses.distinctBy { it.questionId }
    }

    /**
     * less efficient version
     * (
     *   since bring all entity field of question,
     *   join all answers of the connected question,
     *   also fetch for user field in question and answer entity,
     *   also search keyword for answers (outside of query) one more time.
     *   in memory fetchjoin
     *   can be out of memory when question has very many answers
     * )
     * but accurate pagination
     */
    // override fun searchandorderpagination(order: search_order, isclosed: boolean?, keyword: string, amount: long, pagenum: long): list<searchresponse> {
    //     val questionentitylist = queryfactory
    //         .selectfrom(questionentity)
    //         .leftjoin(questionentity.answers, answerentity).fetchjoin()
    //         .leftjoin(questionentity.user, userentity).fetchjoin()
    //         .leftjoin(answerentity.user, userentity).fetchjoin()
    //         .where(
    //             questionentity.title.contains(keyword) // search for keywords
    //                 .or((questionentity.content.contains(keyword)))
    //                 .or((answerentity.content.contains(keyword))),
    //             when (isclosed) { // filter with close
    //                 true -> questionentity.close.eq(true)
    //                 false -> questionentity.close.eq(false)
    //                 null -> null
    //             }
    //         )
    //         .orderby(
    //             when (order) { // order by date or like
    //                 search_order.date -> questionentity.createdat.desc()
    //                 search_order.like -> questionentity.likecount.desc()
    //                 search_order.answer -> questionentity.answercount.desc()
    //             },
    //             questionentity.id.desc(),
    //             answerentity.id.asc(),
    //         )
    //         .offset(pagenum*amount) // pagination
    //         .limit(amount)
    //         .fetch()
    //
    //     return questionentitylist.map {
    //         searchresponse(
    //             it.id,
    //             it.title!!,
    //             it.content,
    //             it.answers.find { answer -> answer.content.contains(keyword) }?.content,
    //             it.answercount,
    //             it.likecount
    //         )
    //     }
    // }
}
