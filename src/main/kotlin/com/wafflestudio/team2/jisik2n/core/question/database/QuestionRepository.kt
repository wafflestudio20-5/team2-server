package com.wafflestudio.team2.jisik2n.core.question.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.common.SearchOrderType
import com.wafflestudio.team2.jisik2n.core.answer.database.QAnswerEntity.answerEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QQuestionEntity.questionEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.SearchResponse
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
    fun searchAndOrderPagination(
        order: SearchOrderType,
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
     * Most Improved Version:
     *     Uses Only 2 Queries,
     *     Minimize cost of in-memory mapping by ordering the tuples with same criteria,
     *     Return Accurate pagination
     */
    override fun searchAndOrderPagination(order: SearchOrderType, isClosed: Boolean?, keyword: String, amount: Long, pageNum: Long): List<SearchResponse> {
        // Query distinctive selection based on question entity only
        val tupleQuestionList = queryFactory.select(
            questionEntity.id,
            questionEntity.title,
            questionEntity.content,
            questionEntity.answerCount,
            questionEntity.likeCount,
            questionEntity.createdAt,
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
                    SearchOrderType.DATE -> questionEntity.createdAt.desc()
                    SearchOrderType.LIKE -> questionEntity.likeCount.desc()
                    SearchOrderType.ANSWER -> questionEntity.answerCount.desc()
                },
                questionEntity.id.desc(),
            )
            .distinct()
            .offset(pageNum * amount) // pagination
            .limit(amount)
            .fetch()

        // Additional query for getting answer content
        val tupleAnswerList = queryFactory
            .select(questionEntity.id, answerEntity.content)
            .from(answerEntity)
            .rightJoin(answerEntity.question, questionEntity)
            .where(
                questionEntity.id.`in`(tupleQuestionList.map { it[questionEntity.id] }),
                answerEntity.content.contains(keyword)
            )
            .orderBy(
                when (order) { // Order by date or like
                    SearchOrderType.DATE -> questionEntity.createdAt.desc()
                    SearchOrderType.LIKE -> questionEntity.likeCount.desc()
                    SearchOrderType.ANSWER -> questionEntity.answerCount.desc()
                },
                questionEntity.id.desc(),
                answerEntity.id.asc(),
            )
            .fetch()

        // Map to SearchResponse with queried tuples
        val searchResponses = tupleQuestionList.map { tq ->
            SearchResponse(
                tq[questionEntity.id]!!,
                tq[questionEntity.title]!!,
                tq[questionEntity.content]!!,
                let {
                    val answerTupleListOfQuestion = tupleAnswerList
                        .filter { ta -> ta[questionEntity.id] == tq[questionEntity.id] }
                    if (answerTupleListOfQuestion.isNotEmpty()) {
                        tupleAnswerList.removeAll(answerTupleListOfQuestion)
                        answerTupleListOfQuestion.first()[answerEntity.content]
                    } else {
                        null
                    }
                },
                tq[questionEntity.answerCount]!!,
                tq[questionEntity.likeCount]!!,
                tq[questionEntity.createdAt]!!
            )
        }

        return searchResponses
    }

    /**
     * Most Efficient Version, but inaccurate pagination
     *      it only guarantees that number of dto is less or equal to amount
     *      sometimes it returns duplicated question
     *      but only use 1 query
     */
    // override fun searchAndOrderPagination(order: SearchOrderType, isClosed: Boolean?, keyword: String, amount: Long, pageNum: Long): List<SearchResponse> {
    //     val searchResponses = queryFactory.select(
    //             QSearchResponse(
    //                 questionEntity.id,
    //                 questionEntity.title,
    //                 questionEntity.content,
    //                 answerEntity.content,
    //                 questionEntity.answerCount,
    //                 questionEntity.likeCount,
    //             )
    //         ).from(questionEntity)
    //         .leftJoin(questionEntity.answers, answerEntity)
    //         .where(
    //             questionEntity.title.contains(keyword) // Search for keywords
    //                 .or((questionEntity.content.contains(keyword)))
    //                 .or((answerEntity.content.contains(keyword))),
    //             when (isClosed) { // Filter with close
    //                 true -> questionEntity.close.eq(true)
    //                 false -> questionEntity.close.eq(false)
    //                 null -> null
    //             }
    //         )
    //         .orderBy(
    //             when (order) { // Order by date or like
    //                 SearchOrderType.DATE -> questionEntity.createdAt.desc()
    //                 SearchOrderType.LIKE -> questionEntity.likeCount.desc()
    //                 SearchOrderType.ANSWER -> questionEntity.answerCount.desc()
    //             },
    //             questionEntity.id.desc(),
    //             answerEntity.id.asc(),
    //         )
    //         .offset(pageNum * amount) // pagination
    //         .limit(amount)
    //         .fetch()
    //
    //     return searchResponses.distinctBy { it.questionId }
    //         .also { println("----------------------------------------------------") }
    // }

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
    /*
    override fun searchAndOrderPagination(order: SearchOrderType, isClosed: Boolean?, keyword: String, amount: Long, pagenum: Long): List<SearchResponse> {
        val questionEntityList = queryFactory
            .selectFrom(questionEntity)
            .leftJoin(questionEntity.answers, answerEntity)
            .leftJoin(questionEntity.user, userEntity)
            .leftJoin(answerEntity.user, userEntity)
            .where(
                questionEntity.title.contains(keyword) // search for keywords
                    .or((questionEntity.content.contains(keyword)))
                    .or((answerEntity.content.contains(keyword))),
                when (isClosed) { // filter with close
                    true -> questionEntity.close.eq(true)
                    false -> questionEntity.close.eq(false)
                    null -> null
                }
            )
            .orderBy(
                when (order) { // order by date or like
                    SearchOrderType.DATE -> questionEntity.createdAt.desc()
                    SearchOrderType.LIKE -> questionEntity.likeCount.desc()
                    SearchOrderType.ANSWER -> questionEntity.answerCount.desc()
                },
                questionEntity.id.desc(),
                answerEntity.id.asc(),
            )
            .offset(pagenum*amount) // pagination
            .limit(amount)
            .fetch()


        return questionEntityList.map {
            SearchResponse(
                it.id,
                it.title!!,
                it.content,
                it.answers.find { answer -> answer.content.contains(keyword) }?.content,
                it.answerCount,
                it.likeCount
            )
        }
    }
     */
}
