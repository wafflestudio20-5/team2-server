package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.dto.QuestionDto
import com.wafflestudio.team2.jisik2n.core.question.dto.UpdateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface QuestionService {
    fun searchQuestion(): MutableList<QuestionDto>
    fun getQuestion(questionId: Long): QuestionDto
    fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionDto
    fun updateQuestion(questionId: Long, request: UpdateQuestionRequest, userEntity: UserEntity): QuestionDto
}

@Service
class QuestionServiceImpl(
    private val questionRepository: QuestionRepository,
    private val photoRepository: PhotoRepository,
) : QuestionService {
    @Transactional
    override fun searchQuestion(): MutableList<QuestionDto> {
        return questionRepository.findAll()
            .map { QuestionDto.of(it) }
            .toMutableList()
    }

    @Transactional
    override fun getQuestion(questionId: Long): QuestionDto {
        val question: Optional<QuestionEntity> = questionRepository.findById(questionId)
        if (question.isEmpty) throw Jisik2n400("존재하지 않는 질문 번호 입니다.(questionId: $questionId)")

        return QuestionDto.of(question.get())
    }

    @Transactional
    override fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionDto {
        val newQuestion = QuestionEntity(
            title = request.title,
            content = request.content,
            user = userEntity,
        )

        request.photos
            .mapIndexed { idx: Int, path: String -> PhotoEntity(path, idx, question = newQuestion) }
            .also { newQuestion.photos.addAll(it) }

        questionRepository.save(newQuestion)

        return QuestionDto.of(newQuestion)
    }

    @Transactional
    override fun updateQuestion(questionId: Long, request: UpdateQuestionRequest, userEntity: UserEntity): QuestionDto {
        val question: Optional<QuestionEntity> = questionRepository.findById(questionId)
        if (question.isEmpty) throw Jisik2n400("존재하지 않는 질문 번호 입니다.(questionId: $questionId)")

        val questionEntity = question.get()
        if (questionEntity.user.id != userEntity.id) throw Jisik2n401("질문 수정은 작성자만 가능합니다.")

        questionEntity.title = request.title ?: questionEntity.title
        questionEntity.content = request.content ?: questionEntity.content

        questionEntity.photos
            .filter { !request.photos.contains(it.path) }
            .let {
                questionEntity.photos.removeAll(it.toSet())
                photoRepository.deleteAll(it)
            }

        // Add photo, and update positions
        request.photos.forEachIndexed { index: Int, path: String ->
            questionEntity.photos
                .find { it.path == path }
                ?. let { it.photosOrder = index }
                ?: PhotoEntity(path, index, question = questionEntity)
                    .also { questionEntity.photos.add(it) }
        }

        questionRepository.save(questionEntity)

        return QuestionDto.of(questionEntity)
    }
}
