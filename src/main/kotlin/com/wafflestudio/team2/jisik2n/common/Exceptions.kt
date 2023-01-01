package com.wafflestudio.team2.jisik2n.common

import org.springframework.http.HttpStatus

open class Jisik2nException(msg: String, val status: HttpStatus) : RuntimeException(msg)

class Jisik2n400(msg: String) : Jisik2nException(msg, HttpStatus.BAD_REQUEST)
class Jisik2n401(msg: String) : Jisik2nException(msg, HttpStatus.UNAUTHORIZED)
class Jisik2n404(msg: String) : Jisik2nException(msg, HttpStatus.NOT_FOUND)

class Jisik2n409(msg: String) : Jisik2nException(msg, HttpStatus.CONFLICT)
