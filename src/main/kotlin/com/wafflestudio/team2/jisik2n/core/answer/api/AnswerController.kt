package com.wafflestudio.team2.jisik2n.core.answer.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerRequest
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerService
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
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
    @GetMapping("/{questionId}")
    fun getAnswers(
        @PathVariable(required = true) questionId: Long,
    ) = answerService.getAnswersOfQuestion(questionId)

    @Authenticated
    @PostMapping("/{questionId}")
    fun postAnswer(
        @UserContext loginUser: UserEntity,
        @PathVariable(required = true) questionId: Long,
        @Valid @RequestBody answerRequest: AnswerRequest,
    ) = let {
        answerService.createAnswer(loginUser, questionId, answerRequest)
        ResponseEntity<String>("Created", HttpStatus.OK)
    }

    @Authenticated
    @PutMapping("/{answerId}")
    fun putAnswer(
        @UserContext loginUser: UserEntity,
        @PathVariable(required = true) answerId: Long,
        @Valid @RequestBody answerRequest: AnswerRequest,
    ) = let {
        answerService.updateAnswer(loginUser, answerId, answerRequest)
        ResponseEntity<String>("Updated", HttpStatus.OK)
    }

    @Authenticated
    @PutMapping("/{answerId}/select/{toSelect}")
    fun selectAnswer(
        @UserContext loginUser: UserEntity,
        @PathVariable(required = true) answerId: Long,
        @PathVariable(required = true) toSelect: Boolean,
    ) = let {
        answerService.toggleSelectAnswer(loginUser, answerId, toSelect)
        ResponseEntity<String>(if (toSelect) { "Selected" } else { "Unselected" }, HttpStatus.OK)
    }

    @Authenticated
    @DeleteMapping("/{answerId}")
    fun deleteAnswer(
        @UserContext loginUser: UserEntity,
        @PathVariable(required = true) answerId: Long,
    ): ResponseEntity<String> {
        answerService.removeAnswer(loginUser, answerId)
        return ResponseEntity<String>("Deleted", HttpStatus.OK)
    }
}
