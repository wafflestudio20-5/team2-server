package com.wafflestudio.team2.jisik2n.core.question.api.request

data class CreateQuestionRequest (
    val title: String,
    val content: String,
    val photo: String?,
)