package com.wafflestudio.team2.jisik2n.core.userQuestionLike.dto

import com.wafflestudio.team2.jisik2n.core.question.dto.QuestionDto
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service

data class UserQuestionLikeDto(
    val id: Long,
    val userId: Long,
    val question: QuestionDto,
) {
    companion object {
        fun of(entity: UserQuestionLikeEntity, s3Service: S3Service): UserQuestionLikeDto {
            return UserQuestionLikeDto(
                id = entity.id,
                userId = entity.user.id,
                question = QuestionDto.of(entity.question, s3Service),
            )
        }
    }
}
