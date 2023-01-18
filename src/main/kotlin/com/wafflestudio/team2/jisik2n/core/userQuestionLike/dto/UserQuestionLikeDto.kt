package com.wafflestudio.team2.jisik2n.core.userQuestionLike.dto

import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity

data class UserQuestionLikeDto(
    val id: Long,
    val userId: Long,
    val questionId: Long,
) {
    companion object {
        fun of(entity: UserQuestionLikeEntity): UserQuestionLikeDto {
            return UserQuestionLikeDto(
                id = entity.id,
                userId = entity.user.id,
                questionId = entity.question.id,
            )
        }
    }
}
