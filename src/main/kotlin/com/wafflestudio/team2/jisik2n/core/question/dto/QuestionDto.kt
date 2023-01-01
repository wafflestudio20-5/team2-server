package com.wafflestudio.team2.jisik2n.core.question.dto

import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import java.time.LocalDateTime

data class QuestionDto(
    val id: Long,
    val title: String?,
    val content: String,
    val username: String,
    val profileImagePath: String? = null,
    val photos: List<String> = emptyList(),
    val answerCount: Int,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
) {
    companion object {
        fun of(entity: QuestionEntity): QuestionDto = entity.run {
            QuestionDto(
                id = this.id,
                title = this.title,
                content = this.content,
                username = this.user.username,
                profileImagePath = this.user.profileImage,
                photos = this.photos.map { it.path },
                answerCount = this.answers.size,
                createdAt = this.createdAt,
                modifiedAt = this.modifiedAt,
            )
        }
    }
}
