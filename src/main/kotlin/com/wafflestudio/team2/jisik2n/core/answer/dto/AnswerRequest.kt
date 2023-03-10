package com.wafflestudio.team2.jisik2n.core.answer.dto

import javax.validation.constraints.NotBlank

data class AnswerRequest(
    @field: NotBlank(message = "내용은 비어있을 수 없습니다.")
    val content: String? = null,
    val photos: List<String> = emptyList(),
)
