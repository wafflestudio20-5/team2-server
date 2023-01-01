package com.wafflestudio.team2.jisik2n.core.answer.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n403
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerRequest
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.transaction.Transactional

interface AnswerService {
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

    fun removeAnswer(loginUser: UserEntity, answerId: Long)
}

@Service
class AnswerServiceImpl(
    private val answerRepository: AnswerRepository,
    private val questionRepository: QuestionRepository,
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository,
) : AnswerService {
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

        // Add newAnswer to question
        question.let {
            it.answers.add(newAnswer)
            questionRepository.save(it)
        }

        // Add newAnswer to user answers
        loginUser.let {
            it.answers.add(newAnswer)
            userRepository.save(it)
        }
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
        if (answer.selected) {
            throw Jisik2n403("채택된 질문은 수정될 수 없습니다.")
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
    override fun removeAnswer(loginUser: UserEntity, answerId: Long) {
        val answer = answerRepository.findByIdOrNull(answerId)

        if (answer != null) {
            if (answer.user.id != loginUser.id) {
                throw Jisik2n403("자신의 게시물만 삭제할 수 있습니다.")
            }
            if (answer.selected) {
                throw Jisik2n403("채택된 질문은 삭제될 수 없습니다.")
            }
            answerRepository.deleteById(answerId)
        }
    }
}
