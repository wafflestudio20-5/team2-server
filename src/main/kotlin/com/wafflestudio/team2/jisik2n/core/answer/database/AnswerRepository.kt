package com.wafflestudio.team2.jisik2n.core.answer.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.core.answer.database.QAnswerEntity.answerEntity
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerResponse
import com.wafflestudio.team2.jisik2n.core.photo.database.QPhotoEntity.photoEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QQuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QQuestionEntity.questionEntity
import com.wafflestudio.team2.jisik2n.core.user.database.QUserEntity.userEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.AnswersOfMyAllProfile
import com.wafflestudio.team2.jisik2n.core.user.dto.AnswersOfMyAnswers
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.QUserAnswerInteractionEntity.userAnswerInteractionEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.dto.UserAnswerInteractionCountResponse
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<AnswerEntity, Long>, CustomAnswerRepository {
    fun findFirstByUserOrderByCreatedAt(user: UserEntity): AnswerEntity?

    fun findAllByUser(user: UserEntity): List<AnswerEntity>
}

interface CustomAnswerRepository {
    fun getAnswerOfQuestionId(
        questionId: Long,
        loginUser: UserEntity?
    ): List<AnswerResponse>

    fun getAnswersOfMyAnswers(username: String): List<AnswersOfMyAnswers>

    fun getAnswersOfMyAllProfile(username: String): List<AnswersOfMyAllProfile>
}

@Component
class CustomAnswerRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
    private val s3Service: S3Service,
) : CustomAnswerRepository {
    override fun getAnswerOfQuestionId(
        questionId: Long,
        loginUser: UserEntity?
    ): List<AnswerResponse> {
//        println("===0")
        // Find answers
        val answers = queryFactory.select(answerEntity)
            .from(answerEntity)
            // join question
            .join(answerEntity.question, questionEntity).fetchJoin()
            // join user
            .join(answerEntity.user, userEntity).fetchJoin()
            // join userAnswerInteraction
            .leftJoin(answerEntity.userAnswerInteractions, userAnswerInteractionEntity).fetchJoin()
            // join photo
            .leftJoin(answerEntity.photos, photoEntity).fetchJoin()
            .where(questionEntity.id.eq(questionId))
            .orderBy(
                answerEntity.selected.desc(),
                answerEntity.createdAt.asc()
            )
            .distinct()
            .fetch()
//        println("===1")

        // Join userAnswerInteraction to answer
        queryFactory.selectFrom(userAnswerInteractionEntity)
            .join(userAnswerInteractionEntity.user, userEntity).fetchJoin()
            .join(userAnswerInteractionEntity.answer, answerEntity).fetchJoin()
            .where(answerEntity.`in`(answers))
            .fetch()
//        println("===2")

        // Find user`s most recent answer date
        val userRecentAnswerDateTuple = queryFactory
            .select(
                userEntity.id,
                answerEntity.createdAt.min()
            )
            .from(answerEntity)
            .join(answerEntity.user, userEntity)
            .groupBy(
                answerEntity.user.id,
                answerEntity.selected,
                answerEntity.createdAt,
            )
            .where(userEntity.`in`(answers.map { it.user }))
            .orderBy(
                answerEntity.selected.desc(),
                answerEntity.createdAt.asc(),
            )
            .fetch()
//        println("===3")

        return answers.map {
            AnswerResponse(
                id = it.id,
                content = it.content,
                photos = it.photos.sortedBy {
                    it.photosOrder
                }.map {
                    s3Service.getUrlFromFilename(it.path)
                },
                createdAt = it.createdAt!!,
                selected = it.selected,
                selectedAt = it.selectedAt,
                interactionCount = let { _ ->
                    val agreeCnt = it.userAnswerInteractions.count { it.isAgree }
                    val disagreeCnt = it.userAnswerInteractions.size - agreeCnt
                    UserAnswerInteractionCountResponse(
                        agree = agreeCnt,
                        disagree = disagreeCnt,
                    )
                },
                userId = it.user.id,
                username = it.user.username,
                profileImagePath = it.user.profileImage
                    ?.let { path -> s3Service.getUrlFromFilename(path) },
                userRecentAnswerDate = userRecentAnswerDateTuple.find { t -> it.user.id == t[userEntity.id] }!!
                    .let { t -> t[answerEntity.createdAt.min()]!! },
                userIsAgreed = it.userAnswerInteractions
                    .find { userAnswerInteraction -> userAnswerInteraction.user == loginUser }
                    ?.run { isAgree }
            )
        }
    }

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
