package com.wafflestudio.team2.jisik2n.core.userQuestionLike.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeRepository
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.dto.UserQuestionLikeDto
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserQuestionLikeService {
    fun getLikeQuestion(userEntity: UserEntity): List<UserQuestionLikeDto>
    fun putLike(user: UserEntity, questionId: Long): UserQuestionLikeDto
}

@Service
class UserQuestionLikeServiceImpl(
    private val userQuestionLikeRepository: UserQuestionLikeRepository,
    private val questionRepository: QuestionRepository,
    private val s3Service: S3Service,
) : UserQuestionLikeService {
    @Transactional
    override fun getLikeQuestion(userEntity: UserEntity): List<UserQuestionLikeDto> {
        return userEntity.userQuestionLikes.map { UserQuestionLikeDto.of(it, s3Service) }
    }

    @Transactional
    override fun putLike(user: UserEntity, questionId: Long): UserQuestionLikeDto {
        val question = questionRepository.findByIdOrNull(questionId)
            ?: throw Jisik2n404("존재하지 않는 질문 번호 입니다.(questionId: $questionId)")

        val userQuestionLikeEntity = userQuestionLikeRepository.findByQuestionAndUser(question, user)

        if (userQuestionLikeEntity != null) {
            userQuestionLikeRepository.delete(userQuestionLikeEntity)
            user.userQuestionLikes.remove(userQuestionLikeEntity)
            question.userQuestionLikes.remove(userQuestionLikeEntity)
            question.likeCount--

            return UserQuestionLikeDto.of(userQuestionLikeEntity, s3Service)
        }

        val newUserQuestionLikeEntity = UserQuestionLikeEntity(
            question = question,
            user = user,
        )
        userQuestionLikeRepository.save(newUserQuestionLikeEntity)
        question.userQuestionLikes.add(newUserQuestionLikeEntity)
        user.userQuestionLikes.add(newUserQuestionLikeEntity)
        question.likeCount++

        return UserQuestionLikeDto.of(newUserQuestionLikeEntity, s3Service)
    }
}
