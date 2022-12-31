package com.wafflestudio.team2.jisik2n.core.answer.service

import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.answer.dto.CreateAnswerRequest
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
        createAnswerRequest: CreateAnswerRequest
    )

    fun updateAnswer(
        loginUser: UserEntity,
        answerId: Long,
        createAnswerRequest: CreateAnswerRequest
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
        createAnswerRequest: CreateAnswerRequest
    ) {
        // Get target question
        val question = questionRepository.findByIdOrNull(questionId)
            ?: TODO("Throw 404 Exception")

        // Add new answer
        var newAnswer = createAnswerRequest.let {
            AnswerEntity(
                content = it.content!!,
                user = loginUser,
                question = question,
            )
        }

        // Add photos to newAnswer
        createAnswerRequest.photos.map { path: String ->
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
        createAnswerRequest: CreateAnswerRequest
    ) {
        val answer = answerRepository.findByIdOrNull(answerId)
            ?: TODO("Throw 404 Not Found Exception")

        if (answer.user.id != loginUser.id || answer.selected) {
            TODO("Throw 403 Forbidden Exception")
        }

        // Update content
        answer.content = createAnswerRequest.content!!

        // Remove photo deleted
        answer.photos.filter { !createAnswerRequest.photos.contains(it.path) }
            .let {
                answer.photos.removeAll(it)
                photoRepository.deleteAll(it)
            }

        // Add photo, and update positions
        createAnswerRequest.photos.mapIndexed { index: Int, path: String ->
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
            if (answer.user.id != loginUser.id || answer.selected) {
                TODO("Throw 403 Forbidden Exception")
            }
            answerRepository.deleteById(answerId)
        }
    }
}
