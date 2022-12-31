package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoService
import com.wafflestudio.team2.jisik2n.core.question.api.request.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val photoService: PhotoService,
) {
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionRepository.findAll()
    }

    fun createQuestion(request: CreateQuestionRequest, user: UserEntity): QuestionEntity {
        val newPhotoList = mutableListOf<PhotoEntity>()
        val newPhoto: PhotoEntity? = if (request.photo != null) photoService.createPhoto(request.photo) else null
        if (newPhoto != null) {
            newPhotoList.add(newPhoto)
        }

        val newQuestion = QuestionEntity(
            title = request.title,
            content = request.content,
            photos = newPhotoList,
            user = user,
        )
        questionRepository.save(newQuestion)

        return newQuestion
    }
}
