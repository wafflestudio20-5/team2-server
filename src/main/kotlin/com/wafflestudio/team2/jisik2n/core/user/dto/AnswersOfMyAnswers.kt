package com.wafflestudio.team2.jisik2n.core.user.dto

import java.time.LocalDateTime

data class AnswersOfMyAnswers(
    val questionId: Long,
    val title: String,
    val createdAt: LocalDateTime
)
