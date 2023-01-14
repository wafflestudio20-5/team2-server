package com.wafflestudio.team2.jisik2n.core.user.dto

import java.time.LocalDateTime

data class AnswersOfMyAllProfile(
    val id: Long,
    val content: String,
    // val photos: List<String>,
    val createdAt: LocalDateTime,
    val selected: Boolean,
    val selectedAt: LocalDateTime?
)
