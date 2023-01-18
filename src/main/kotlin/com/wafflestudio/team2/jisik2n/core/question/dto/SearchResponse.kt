package com.wafflestudio.team2.jisik2n.core.question.dto

import com.querydsl.core.annotations.QueryProjection

data class SearchResponse @QueryProjection constructor(
    val questionId: Long,
    val title: String,
    val content: String,
    val answerContent: String?,
    var answerCount: Long,
    var questionLikeCount: Long,
)
