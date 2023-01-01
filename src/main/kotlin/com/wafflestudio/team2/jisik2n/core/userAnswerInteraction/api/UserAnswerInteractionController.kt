package com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.api

import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.service.UserAnswerInteractionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/userAnswerInteraction")
class UserAnswerInteractionController(
    private val userAnswerInteractionService: UserAnswerInteractionService,
) {
    @GetMapping("/count/{answerId}")
    fun getCountOfInteraction(
        @PathVariable answerId: Long,
    ) = userAnswerInteractionService.getCountOfInteraction(answerId)
}
