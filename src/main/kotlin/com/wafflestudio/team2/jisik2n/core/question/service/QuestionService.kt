package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
) {
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionRepository.findAll()
    }

    fun createQuestion(request: CreateQuestionRequest, user: UserEntity): QuestionEntity {
        val newQuestion = QuestionEntity(
            title = request.title,
            content = request.content,
            user = user,
        )

        request.photos
            .map { path: String -> PhotoEntity(path, question = newQuestion) }
            .also { newQuestion.photos.addAll(it) }

        questionRepository.save(newQuestion)

        return newQuestion
    }
}
