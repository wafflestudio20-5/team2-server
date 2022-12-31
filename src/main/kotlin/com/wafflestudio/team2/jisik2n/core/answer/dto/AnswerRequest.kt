package com.wafflestudio.team2.jisik2n.core.answer.dto

import javax.validation.constraints.NotBlank

data class AnswerRequest(
    @field: NotBlank(message = "Content should not be empty")
    val content: String? = null,
    val photos: List<String> = emptyList(),
)
