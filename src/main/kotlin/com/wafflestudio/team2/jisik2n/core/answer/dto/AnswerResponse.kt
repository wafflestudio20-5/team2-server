package com.wafflestudio.team2.jisik2n.core.answer.dto

import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.dto.UserAnswerInteractionCountResponse
import java.time.LocalDateTime

data class AnswerResponse(
    val id: Long,
    val content: String,
    val photos: List<String> = emptyList(),
    val createdAt: LocalDateTime,
    val selected: Boolean = false,
    val selectedAt: LocalDateTime? = null,
    val interactionCount: UserAnswerInteractionCountResponse,
    val userId: Long,
    val username: String,
    val profileImagePath: String? = null,
    val userRecentAnswerDate: LocalDateTime,
    val userIsAgreed: Boolean? = null,
)
