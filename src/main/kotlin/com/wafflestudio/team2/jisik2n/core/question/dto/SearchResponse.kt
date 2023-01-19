package com.wafflestudio.team2.jisik2n.core.question.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class SearchResponse @QueryProjection constructor(
    val questionId: Long,
    val title: String,
    val content: String,
    val answerContent: String?,
    val answerCount: Long,
    val questionLikeCount: Long,
    val questionCreatedAt: LocalDateTime,
)
