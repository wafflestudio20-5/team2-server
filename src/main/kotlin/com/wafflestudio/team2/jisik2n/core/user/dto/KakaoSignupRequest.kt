package com.wafflestudio.team2.jisik2n.core.user.dto

import javax.validation.constraints.NotBlank

data class KakaoSignupRequest(
    @NotBlank
    val snsId: String?,

    @NotBlank
    val username: String,

    @NotBlank
    val isMale: Boolean?
)
