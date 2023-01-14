package com.wafflestudio.team2.jisik2n.core.photo.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class PhotoRequest(
    @field: NotBlank
    val url: String,
    @field: NotNull
    val position: Int? = null
)
