package com.wafflestudio.team2.jisik2n.core.user.dto

import java.time.LocalDateTime

data class QuestionsOfMyQuestions(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val answerCount: Long
)
