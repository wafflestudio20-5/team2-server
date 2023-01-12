package com.wafflestudio.team2.jisik2n.core.user.dto

import java.time.LocalDateTime

data class QuestionsOfMyAllProfile(
    val id: Long,
    val title: String,
    val content: String,
    // val photos: List<String>,
    // val answerNumber: Int,
    val createdAt: LocalDateTime?,
    val close: Boolean,
    val closedAt: LocalDateTime?
    // val userQuestionLikeNumber: Int
)
