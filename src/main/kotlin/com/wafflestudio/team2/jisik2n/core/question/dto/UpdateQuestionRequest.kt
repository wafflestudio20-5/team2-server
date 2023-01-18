package com.wafflestudio.team2.jisik2n.core.question.dto

import com.wafflestudio.team2.jisik2n.core.photo.dto.PhotoRequest

data class UpdateQuestionRequest(
    val title: String?,
    val content: String?,
    val tag: List<String>?,
    val photos: List<PhotoRequest> = emptyList(),
)
