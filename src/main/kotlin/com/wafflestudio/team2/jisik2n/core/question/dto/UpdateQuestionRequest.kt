package com.wafflestudio.team2.jisik2n.core.question.dto

data class UpdateQuestionRequest(
    val title: String?,
    val content: String?,
    val photos: List<String> = emptyList(),
)
