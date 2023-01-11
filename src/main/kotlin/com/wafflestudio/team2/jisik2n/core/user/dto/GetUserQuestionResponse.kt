package com.wafflestudio.team2.jisik2n.core.user.dto

data class GetUserQuestionResponse(
    val id: Long,
    val username: String,
    val questions: List<Questions>
)
