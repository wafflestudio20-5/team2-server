package com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.service.UserAnswerInteractionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/userAnswerInteraction")
class UserAnswerInteractionController(
    private val userAnswerInteractionService: UserAnswerInteractionService,
) {
    @GetMapping("/count/{answerId}")
    fun getCountOfInteraction(
        @PathVariable answerId: Long,
    ) = userAnswerInteractionService.getCountOfInteraction(answerId)

    @Authenticated
    @PutMapping("/{answerId}/{isAgree}")
    fun putInteraction(
        @UserContext loginUser: UserEntity,
        @PathVariable answerId: Long,
        @PathVariable isAgree: Boolean,
    ): ResponseEntity<String> {
        userAnswerInteractionService.putInteraction(loginUser, answerId, isAgree)
        return ResponseEntity<String>("Interacted", HttpStatus.OK)
    }
}
