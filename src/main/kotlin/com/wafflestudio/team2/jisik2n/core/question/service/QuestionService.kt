package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.common.*
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoService
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.dto.QuestionDto
import com.wafflestudio.team2.jisik2n.core.question.dto.SearchResponse
import com.wafflestudio.team2.jisik2n.core.question.dto.UpdateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface QuestionService {
    fun searchQuestion(
        order: SearchOrderType,
        isClosed: Boolean? = null,
        keyword: String,
        amount: Long = 20,
        pageNum: Long = 0
    ): List<SearchResponse>
    fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionDto
    fun getQuestion(questionId: Long): QuestionDto
    fun updateQuestion(questionId: Long, request: UpdateQuestionRequest, userEntity: UserEntity): QuestionDto
    fun deleteQuestion(questionId: Long, userEntity: UserEntity)
    fun getRandomQuestion(): QuestionDto
    fun getAdminQuestion(): QuestionDto
}

@Service
class QuestionServiceImpl(
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    private val photoService: PhotoService,
    private val s3Service: S3Service,
) : QuestionService {

    @Transactional
    override fun searchQuestion(
        order: SearchOrderType,
        isClosed: Boolean?,
        keyword: String,
        amount: Long,
        pageNum: Long
    ): List<SearchResponse> {
        return questionRepository.searchAndOrderPagination(order, isClosed, keyword, amount, pageNum)
    }

    @Transactional
    override fun createQuestion(request: CreateQuestionRequest, userEntity: UserEntity): QuestionDto {
        val newQuestion = QuestionEntity(
            title = request.title,
            content = request.content,
            tag = request.tag.joinToString("/"),
            user = userEntity,
        )

        photoService.initiallyAddPhotos(newQuestion, request.photos)
        questionRepository.save(newQuestion)

        return QuestionDto.of(newQuestion, s3Service)
    }

    override fun getQuestion(questionId: Long) =
        questionRepository.findQuestionDtoByIdOrNull(questionId)
            ?: throw Jisik2n400("???????????? ?????? ?????? ?????? ?????????.(questionId: $questionId)")

    @Transactional
    override fun updateQuestion(questionId: Long, request: UpdateQuestionRequest, userEntity: UserEntity): QuestionDto {
        val question: Optional<QuestionEntity> = questionRepository.findById(questionId)
        if (question.isEmpty) throw Jisik2n400("???????????? ?????? ?????? ?????? ?????????.(questionId: $questionId)")

        val questionEntity = question.get()
        if (questionEntity.user.id != userEntity.id) throw Jisik2n401("?????? ????????? ???????????? ???????????????.")

        questionEntity.title = request.title ?: questionEntity.title
        questionEntity.content = request.content ?: questionEntity.content
        questionEntity.tag = request.tag?.joinToString("/") ?: questionEntity.tag

        // Update Photos
        photoService.modifyPhotos(questionEntity, request.photos)

        questionRepository.save(questionEntity)

        return QuestionDto.of(questionEntity, s3Service)
    }

    @Transactional
    override fun deleteQuestion(questionId: Long, userEntity: UserEntity) {
        val question: Optional<QuestionEntity> = questionRepository.findById(questionId)
        if (question.isEmpty) throw Jisik2n400("???????????? ?????? ?????? ?????? ?????????.(questionId: $questionId)")

        val questionEntity = question.get()
        if (questionEntity.user.id != userEntity.id) throw Jisik2n401("?????? ????????? ???????????? ???????????????.")

        if (questionEntity.close) throw Jisik2n403("????????? ????????? ????????? ??????????????????.")

        // Delete Photos
        photoService.deletePhotos(questionEntity.photos)
        questionEntity.answers.map {
            photoService.deletePhotos(it.photos)
        }

        questionRepository.delete(questionEntity)
    }

    override fun getRandomQuestion(): QuestionDto {
        val totalCount: Long = questionRepository.count()
        val idx = (Math.random() * totalCount).toLong()
        return questionRepository.getIthQuestionDtoOrNull(idx)
            ?: throw Jisik2n409("????????? ??????????????????.")
    }

    override fun getAdminQuestion(): QuestionDto {
        if (!userRepository.existsByUid(ADMIN_UID)) {
            throw Jisik2n404("?????? ????????? ???????????? ????????????")
        }
        return questionRepository.findLatestQuestionDtoByUserUidOrNull(ADMIN_UID)
            ?: throw Jisik2n404("?????? ????????? ???????????? ????????????")
    }
}
