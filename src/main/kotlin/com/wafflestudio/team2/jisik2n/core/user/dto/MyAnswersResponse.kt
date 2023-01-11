package com.wafflestudio.team2.jisik2n.core.user.dto

data class MyAnswersResponse(
    val id: Long,
    val username: String,
    val answers: List<Answers>
)
