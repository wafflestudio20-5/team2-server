package com.wafflestudio.team2.jisik2n.core.answer.api

import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerRequest
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerService
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

interface AnswerController {
    fun postAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        questionId: Long,
        answerRequest: AnswerRequest,
        bindingResult: BindingResult
    ): ResponseEntity<String>

    fun putAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        answerId: Long,
        @RequestBody answerRequest: AnswerRequest,
        bindingResult: BindingResult,
    ): ResponseEntity<String>

    fun deleteAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        answerId: Long,
    ): ResponseEntity<String>
}

@RestController
@RequestMapping("/api/answer")
class AnswerControllerImpl(
    private val answerService: AnswerService,
    private val userRepository: UserRepository,
) : AnswerController {
    @PostMapping("/{questionId}")
    override fun postAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) questionId: Long,
        @Valid @RequestBody answerRequest: AnswerRequest,
        bindingResult: BindingResult,
    ) = if (bindingResult.hasErrors()) {
        TODO("Throw 400 Exception")
    } else {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.createAnswer(loginUser, questionId, answerRequest)
        ResponseEntity<String>("Created", HttpStatus.OK)
    }

    @PutMapping("/{answerId}")
    override fun putAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) answerId: Long,
        @Valid @RequestBody answerRequest: AnswerRequest,
        bindingResult: BindingResult,
    ) = if (bindingResult.hasErrors()) {
        TODO("Throw 400 Exception")
    } else {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.updateAnswer(loginUser, answerId, answerRequest)
        ResponseEntity<String>("Updated", HttpStatus.OK)
    }

    @DeleteMapping("/{answerId}")
    override fun deleteAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) answerId: Long,
    ): ResponseEntity<String> {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.removeAnswer(loginUser, answerId)
        return ResponseEntity<String>("Deleted", HttpStatus.OK)
    }
}
