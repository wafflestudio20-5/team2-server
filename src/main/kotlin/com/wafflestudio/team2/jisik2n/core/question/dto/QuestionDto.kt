package com.wafflestudio.team2.jisik2n.core.question.dto

import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import java.time.LocalDateTime

data class QuestionDto(
    val id: Long,
    val title: String?,
    val content: String,
    val tag: List<String> = emptyList(),
    val username: String,
    val profileImagePath: String? = null,
    val photos: List<String> = emptyList(),
    val answerNumber: Int,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
    val close: Boolean,
    val closedAt: LocalDateTime?,
    val userQuestionLikeNumber: Int,
) {
    companion object {
        fun of(entity: QuestionEntity, s3Service: S3Service): QuestionDto = entity.run {
            QuestionDto(
                id = this.id,
                title = this.title,
                content = this.content,
                tag = if (this.tag == "") listOf() else this.tag.split("/"),
                username = this.user.username,
                profileImagePath = this.user.profileImage,
                photos = this.photos
                    .sortedBy { it.photosOrder }
                    .map { s3Service.getUrlFromFilename(it.path) },
                answerNumber = this.answers.size,
                createdAt = this.createdAt,
                modifiedAt = this.modifiedAt,
                close = this.close,
                closedAt = this.closedAt,
                userQuestionLikeNumber = this.userQuestionLikes.size
            )
        }
    }
}
