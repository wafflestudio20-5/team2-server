package com.wafflestudio.team2.jisik2n.core.question.api.request

import javax.validation.constraints.NotBlank

data class CreateQuestionRequest(
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val content: String,
    val photo: String?,
)
