package com.wafflestudio.team2.jisik2n.core.answer.dto

import java.time.LocalDateTime

data class AnswerResponse(
    val content: String,
    val selected: Boolean = false,
    val selectedAt: LocalDateTime? = null,
    val photos: List<String> = emptyList(),
    val username: String,
    val profileImagePath: String? = null,
    val userRecentAnswerDate: LocalDateTime
)
