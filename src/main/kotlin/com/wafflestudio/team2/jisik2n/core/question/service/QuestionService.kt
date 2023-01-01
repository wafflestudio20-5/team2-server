package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface QuestionService {
    fun searchQuestion(): MutableList<QuestionEntity>
    fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionEntity
}

@Service
class QuestionServiceImpl(
    private val questionRepository: QuestionRepository,
) : QuestionService {
    override fun searchQuestion(): MutableList<QuestionEntity> {
        return questionRepository.findAll()
    }

    @Transactional
    override fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionEntity {
        val newQuestion = QuestionEntity(
            title = request.title,
            content = request.content,
            user = userEntity,
        )

        request.photos
            .map { path: String -> PhotoEntity(path, question = newQuestion) }
            .also { newQuestion.photos.addAll(it) }

        return questionRepository.save(newQuestion)
    }
}
