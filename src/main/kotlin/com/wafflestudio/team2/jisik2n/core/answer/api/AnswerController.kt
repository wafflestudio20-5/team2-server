package com.wafflestudio.team2.jisik2n.core.answer.api

import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerRequest
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerService
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/answer")
class AnswerController(
    private val answerService: AnswerService,
    private val userRepository: UserRepository,
) {
    @PostMapping("/{questionId}")
    fun postAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) questionId: Long,
        @Valid @RequestBody answerRequest: AnswerRequest,
    ) = let {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.createAnswer(loginUser, questionId, answerRequest)
        ResponseEntity<String>("Created", HttpStatus.OK)
    }

    @PutMapping("/{answerId}")
    fun putAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) answerId: Long,
        @Valid @RequestBody answerRequest: AnswerRequest,
    ) = let {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.updateAnswer(loginUser, answerId, answerRequest)
        ResponseEntity<String>("Updated", HttpStatus.OK)
    }

    @PutMapping("/{answerId}/select/{toSelect}")
    fun selectAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) answerId: Long,
        @PathVariable(required = true) toSelect: Boolean,
    ) = let {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.toggleSelectAnswer(loginUser, answerId, toSelect)
        ResponseEntity<String>(if (toSelect) { "Selected" } else { "Unselected" }, HttpStatus.OK)
    }

    @DeleteMapping("/{answerId}")
    fun deleteAnswer(
        // loginUser: UserEntity // TODO: Automatically gives logged in user
        @PathVariable(required = true) answerId: Long,
    ): ResponseEntity<String> {
        val loginUser = userRepository.getReferenceById(1) // Temporary
        answerService.removeAnswer(loginUser, answerId)
        return ResponseEntity<String>("Deleted", HttpStatus.OK)
    }
}
