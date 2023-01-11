package com.wafflestudio.team2.jisik2n.core.user.dto

data class MyQuestionsResponse(
    val id: Long,
    val username: String,
    val questions: List<Questions>
)
