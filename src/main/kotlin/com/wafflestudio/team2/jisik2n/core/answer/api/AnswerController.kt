package com.wafflestudio.team2.jisik2n.core.answer.api

import com.wafflestudio.team2.jisik2n.core.answer.dto.CreateAnswerRequest
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerService
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

interface AnswerController {
    fun postAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        questionId: Long,
        createAnswerRequest: CreateAnswerRequest,
        bindingResult: BindingResult
    ): ResponseEntity<String>

    fun putAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        answerId: Long,
        @RequestBody createAnswerRequest: CreateAnswerRequest,
        bindingResult: BindingResult,
    ): ResponseEntity<String>
}
@RestController
@RequestMapping("/api/answer")
class AnswerControllerImpl(
    private val answerService: AnswerService,
    private val userRepository: UserRepository,
) {
    @PostMapping("/{questionId}")
    fun postAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) questionId: Long,
        @RequestBody createAnswerRequest: CreateAnswerRequest,
        bindingResult: BindingResult,
    ) = if (bindingResult.hasErrors()) {
        TODO("Throw 400 Exception")
    } else {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.createAnswer(loginUser, questionId, createAnswerRequest)
        ResponseEntity<String>("Created", HttpStatus.OK)
    }

    @PutMapping("/{answerId}")
    fun putAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) answerId: Long,
        @RequestBody createAnswerRequest: CreateAnswerRequest,
        bindingResult: BindingResult,
    ) = if (bindingResult.hasErrors()) {
        TODO("Throw 400 Exception")
    } else {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.updateAnswer(loginUser, answerId, createAnswerRequest)
        ResponseEntity<String>("Updated", HttpStatus.OK)
    }
}
