package com.wafflestudio.team2.jisik2n.common

import org.springframework.http.HttpStatus

open class Jisik2nException(msg: String, val status: HttpStatus) : RuntimeException(msg)

class Jisik2n400(msg: String) : Jisik2nException(msg, HttpStatus.BAD_REQUEST)