package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoService
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.dto.QuestionDto
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface QuestionService {
    fun searchQuestion(): MutableList<QuestionDto>
    fun getQuestion(questionId: Long): QuestionDto
    fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionDto
}

@Service
class QuestionServiceImpl(
    private val questionRepository: QuestionRepository,
    private val photoService: PhotoService,
    private val s3Service: S3Service,
) : QuestionService {
    @Transactional
    override fun searchQuestion(): MutableList<QuestionDto> {
        return questionRepository.findAll()
            .map { QuestionDto.of(it, s3Service) }
            .toMutableList()
    }

    @Transactional
    override fun getQuestion(questionId: Long): QuestionDto {
        val question: Optional<QuestionEntity> = questionRepository.findById(questionId)
        if (question.isEmpty) throw Jisik2n400("존재하지 않는 질문 번호 입니다.(questionId: $questionId)")

        return QuestionDto.of(question.get(), s3Service)
    }

    @Transactional
    override fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionDto {
        val newQuestion = QuestionEntity(
            title = request.title,
            content = request.content,
            user = userEntity,
        )

        photoService.initiallyAddPhotos(newQuestion, request.photos)
        // request.photos
        //     .mapIndexed { idx: Int, path: String -> PhotoEntity(path, idx, question = newQuestion) }
        //     .also { newQuestion.photos.addAll(it) }

        questionRepository.save(newQuestion)

        return QuestionDto.of(newQuestion, s3Service)
    }
}
