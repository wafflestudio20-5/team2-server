package com.wafflestudio.team2.jisik2n.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.sql.SQLIntegrityConstraintViolationException

@RestControllerAdvice
class Jisik2nExceptionHandler {

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handle(e: MethodArgumentNotValidException): ResponseEntity<Any> {
        val bindingResult: BindingResult = e.bindingResult
        return ResponseEntity(bindingResult.fieldError?.defaultMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [Jisik2nException::class])
    fun handle(e: Jisik2nException): ResponseEntity<Any> {
        return ResponseEntity(e.message, e.status)
    }

    @ExceptionHandler(value = [SQLIntegrityConstraintViolationException::class])
    fun handle(e: SQLIntegrityConstraintViolationException): ResponseEntity<Any> {
        return ResponseEntity("중복된 값이 있습니다.", HttpStatus.CONFLICT)
    }
}
