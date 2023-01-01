package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
) {
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionRepository.findAll()
    }

    @Transactional
    fun createQuestion(request: CreateQuestionRequest, user: UserEntity): QuestionEntity {
        val newQuestion = QuestionEntity(
            title = request.title,
            content = request.content,
            user = user,
        )

        request.photos
            .map { path: String -> PhotoEntity(path, question = newQuestion) }
            .also { newQuestion.photos.addAll(it) }

        return questionRepository.save(newQuestion)
    }
}
