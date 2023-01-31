package com.wafflestudio.team2.jisik2n.core.user.dto

import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity

data class MyAllProfileResponse(
    val id: Long,
    val uid: String?,
    val snsId: String?,
    val username: String,
    val isMale: Boolean?,
    val profileImage: String?,
    val questions: List<QuestionsOfMyAllProfile>,
    val answers: List<AnswersOfMyAllProfile>
) {
    companion object {
        fun of(
            userEntity: UserEntity,
            questions: List<QuestionsOfMyAllProfile>,
            answers: List<AnswersOfMyAllProfile>,
            getImageUrl: (String) -> String,
        ): MyAllProfileResponse {
            return MyAllProfileResponse(
                id = userEntity.id,
                uid = userEntity.uid,
                snsId = userEntity.snsId,
                username = userEntity.username,
                isMale = userEntity.isMale,
                profileImage = userEntity.profileImage?.let { getImageUrl(it) },
                questions = questions,
                answers = answers
            )
        }
    }
}
