package com.wafflestudio.team2.jisik2n.core.answer.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n403
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerRequest
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerResponse
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoService
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.UserAnswerInteractionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import javax.transaction.Transactional

interface AnswerService {
    fun getAnswersOfQuestion(questionId: Long, loginUser: UserEntity? = null): List<AnswerResponse>

    fun createAnswer(
        loginUser: UserEntity,
        questionId: Long,
        answerRequest: AnswerRequest
    )

    fun updateAnswer(
        loginUser: UserEntity,
        answerId: Long,
        answerRequest: AnswerRequest
    )

    fun toggleSelectAnswer(loginUser: UserEntity, answerId: Long, toSelect: Boolean)

    fun removeAnswer(loginUser: UserEntity, answerId: Long)
}

@Service
class AnswerServiceImpl(
    private val answerRepository: AnswerRepository,
    private val questionRepository: QuestionRepository,
    private val userAnswerInteractionRepository: UserAnswerInteractionRepository,
    private val photoService: PhotoService,
) : AnswerService {
    override fun getAnswersOfQuestion(questionId: Long, loginUser: UserEntity?): List<AnswerResponse> {
        if (!questionRepository.existsById(questionId)) {
            throw Jisik2n404("${questionId}??? ???????????? ????????? ????????????.")
        }
        return answerRepository.getAnswerOfQuestionId(questionId, loginUser)
//        // Get target question
//        val question = questionRepository.findByIdOrNull(questionId)
//            ?: throw Jisik2n404("${questionId}??? ???????????? ????????? ????????????.")
//
//        // TODO: Improve query
//        val answers = question.answers.sortedWith(compareBy({ !it.selected }, { it.createdAt }))
//
//        return answers.map { it.toResponse(loginUser, answerRepository, s3Service, userAnswerInteractionService) }
    }

    @Transactional
    override fun createAnswer(
        loginUser: UserEntity,
        questionId: Long,
        answerRequest: AnswerRequest,
    ) {
        // Get target question
        val question = questionRepository.findByIdOrNull(questionId)
            ?: throw Jisik2n404("${questionId}??? ???????????? ????????? ????????????.")

        if (loginUser.id == question.user.id) {
            throw Jisik2n403("????????? ???????????? ?????? ??? ????????????.")
        }

        if (question.answers.find { it.user.id == loginUser.id } != null) {
            throw Jisik2n400("????????? ???????????? ??? ?????? ?????? ???????????????.")
        }
        // Add new answer
        val newAnswer = AnswerEntity(
            content = answerRequest.content!!,
            user = loginUser,
            question = question,
        )

        // Add 1 to answer count
        question.answerCount++
        question.answers.add(newAnswer)

        // Add photos to newAnswer
        photoService.initiallyAddPhotos(newAnswer, answerRequest.photos)

        answerRepository.save(newAnswer)
    }

    @Transactional
    override fun updateAnswer(
        loginUser: UserEntity,
        answerId: Long,
        answerRequest: AnswerRequest
    ) {
        val answer = answerRepository.findByIdOrNull(answerId)
            ?: throw Jisik2n404("${answerId}??? ???????????? ????????? ????????????")

        if (answer.user.id != loginUser.id) {
            throw Jisik2n403("????????? ???????????? ????????? ??? ????????????.")
        }
        if (answer.question.close) {
            throw Jisik2n403("????????? ????????? ????????? ??? ????????????.")
        }

        // Update content
        answer.content = answerRequest.content!!

        // Update photos
        photoService.modifyPhotos(answer, answerRequest.photos)

        answerRepository.save(answer)
    }

    @Transactional
    override fun toggleSelectAnswer(loginUser: UserEntity, answerId: Long, toSelect: Boolean) {
        val answer = answerRepository.findByIdOrNull(answerId) // TODO: Optimize query (by fetch question-user)
            ?: throw Jisik2n404("$answerId ????????? ???????????? ????????????.")

        if (loginUser.id != answer.question.user.id) {
            throw Jisik2n403("????????? ???????????? ????????? ????????? ??? ????????????.")
        }

        if (answer.selected == toSelect) {
            throw Jisik2n400("????????? ?????? ????????????.")
        }

        if (toSelect) { selectAnswer(answer) } else { unselectAnswer(answer) }
    }

    @Transactional
    fun selectAnswer(answer: AnswerEntity) {
        if (answer.question.close) {
            throw Jisik2n403("????????? ????????? ????????? ????????? ??? ????????????.")
        }

        answer.selected = true
        answer.selectedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        answer.question.close = true
    }

    @Transactional
    fun unselectAnswer(answer: AnswerEntity) {
        answer.selected = false
        answer.question.close = false
    }

    @Transactional
    override fun removeAnswer(loginUser: UserEntity, answerId: Long) {
        val answer = answerRepository.findByIdOrNull(answerId)

        if (answer != null) {
            if (answer.user.id != loginUser.id) {
                throw Jisik2n403("????????? ???????????? ????????? ??? ????????????.")
            }
            if (answer.question.close) {
                throw Jisik2n403("????????? ????????? ????????? ??? ????????????.")
            }

            // Remove Interactions
            userAnswerInteractionRepository.deleteByAnswer(answer)

            // Remove photos from bucket and db
            photoService.deletePhotos(answer.photos)

            // -1 to answer count
            answer.question.answerCount--
            answer.question.answers.remove(answer)

            answerRepository.deleteById(answerId)
        }
    }
}
