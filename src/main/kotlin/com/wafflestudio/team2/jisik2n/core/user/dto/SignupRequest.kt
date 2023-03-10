package com.wafflestudio.team2.jisik2n.core.user.dto

import javax.validation.constraints.NotBlank

data class SignupRequest(

    @NotBlank
    val uid: String,

    @NotBlank
    val password: String,

    @NotBlank
    val username: String,

    @NotBlank
    val isMale: Boolean
)
