package com.wafflestudio.team2.jisik2n.core.question.dto

import javax.validation.constraints.NotBlank

data class CreateQuestionRequest(
    @field:NotBlank(message = "Title 은 비어있을 수 없습니다.")
    val title: String,
    @field:NotBlank(message = "Content 은 비어있을 수 없습니다.")
    val content: String,
    val photos: List<String> = emptyList(),
)
