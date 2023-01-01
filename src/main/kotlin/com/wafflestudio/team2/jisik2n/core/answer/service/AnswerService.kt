package com.wafflestudio.team2.jisik2n.core.answer.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n403
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerRequest
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerResponse
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import javax.transaction.Transactional

interface AnswerService {
    fun getAnswersOfQuestion(questionId: Long): List<AnswerResponse>

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
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository,
) : AnswerService {
    override fun getAnswersOfQuestion(questionId: Long): List<AnswerResponse> {
        // Get target question
        val question = questionRepository.findByIdOrNull(questionId)
            ?: throw Jisik2n404("${questionId}에 해당하는 질문이 없습니다.")

        // TODO: Improve query
        val answers = question.answers.sortedWith(compareBy({ !it.selected }, { it.createdAt }))

        return answers.map { it.toResponse(answerRepository) }
    }

    @Transactional
    override fun createAnswer(
        loginUser: UserEntity,
        questionId: Long,
        answerRequest: AnswerRequest
    ) {
        // Get target question
        val question = questionRepository.findByIdOrNull(questionId)
            ?: throw Jisik2n404("${questionId}에 해당하는 질문이 없습니다.")

        if (loginUser.id == question.user.id) {
            throw Jisik2n403("자신의 질문에는 답할 수 없습니다.")
        }

        // Add new answer
        var newAnswer = answerRequest.let {
            AnswerEntity(
                content = it.content!!,
                user = loginUser,
                question = question,
            )
        }

        // Add photos to newAnswer
        answerRequest.photos.map { path: String ->
            PhotoEntity(path, answer = newAnswer)
        }.also {
            newAnswer.photos.addAll(it)
        }
        newAnswer = answerRepository.save(newAnswer)
    }

    @Transactional
    override fun updateAnswer(
        loginUser: UserEntity,
        answerId: Long,
        answerRequest: AnswerRequest
    ) {
        val answer = answerRepository.findByIdOrNull(answerId)
            ?: throw Jisik2n404("${answerId}에 해당하는 답변이 없습니다")

        if (answer.user.id != loginUser.id) {
            throw Jisik2n403("자신의 게시물만 수정할 수 있습니다.")
        }
        if (answer.question.close) {
            throw Jisik2n403("마감된 질문은 수정될 수 없습니다.")
        }

        // Update content
        answer.content = answerRequest.content!!

        // Remove photo deleted
        answer.photos.filter { !answerRequest.photos.contains(it.path) }
            .let {
                answer.photos.removeAll(it)
                photoRepository.deleteAll(it)
            }

        // Add photo, and update positions
        answerRequest.photos.mapIndexed { index: Int, path: String ->
            answer.photos.find { it.path == path }
                ?. let { // If photo exists, update its position
                    answer.photos.remove(it)
                    answer.photos.add(index, it)
                }
                ?: PhotoEntity(path, answer = answer) // If photo doesn't exist, create new PhotoEntity and add to answer
                    .also { answer.photos.add(index, it) }
        }

        answerRepository.save(answer)
    }

    @Transactional
    override fun toggleSelectAnswer(loginUser: UserEntity, answerId: Long, toSelect: Boolean) {
        val answer = answerRepository.findByIdOrNull(answerId) // TODO: Optimize query (by fetch question-user)
            ?: throw Jisik2n404("$answerId 답변이 존재하지 않습니다.")

        if (loginUser.id != answer.question.user.id) {
            throw Jisik2n403("질문한 사용자만 답변을 채택할 수 있습니다.")
        }

        if (answer.selected == toSelect) {
            throw Jisik2n400("변경할 점이 없습니다.")
        }

        if (toSelect) { selectAnswer(answer) } else { unselectAnswer(answer) }
    }

    @Transactional
    fun selectAnswer(answer: AnswerEntity) {
        if (answer.question.close) {
            throw Jisik2n403("마감된 질문에 답변을 채택할 수 없습니다.")
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
                throw Jisik2n403("자신의 게시물만 삭제할 수 있습니다.")
            }
            if (answer.question.close) {
                throw Jisik2n403("마감된 질문은 삭제될 수 없습니다.")
            }
            answerRepository.deleteById(answerId)
            // TODO: Remove Interactions
        }
    }
}
