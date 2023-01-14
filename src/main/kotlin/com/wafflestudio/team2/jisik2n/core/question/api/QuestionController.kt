package com.wafflestudio.team2.jisik2n.core.question.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.dto.QuestionDto
import com.wafflestudio.team2.jisik2n.core.question.dto.UpdateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RequestMapping("/api/question")
@RestController
class QuestionController(
    private val questionService: QuestionService,
) {
    @GetMapping("/search")
    fun searchQuestion(
        @RequestParam(required = false, defaultValue = "date") order: String,
        @RequestParam(required = false, defaultValue = "null") isClosed: String,
        @RequestParam(required = false, defaultValue = "") query: String,
    ): MutableList<QuestionDto> {
        return questionService.searchQuestion(order, isClosed, query)
    }

    @Authenticated
    @PostMapping("/")
    fun createQuestion(
        @Valid @RequestBody request: CreateQuestionRequest,
        @UserContext userEntity: UserEntity,
    ): QuestionDto {
        return questionService.createQuestion(request, userEntity)
    }

    @PutMapping("/{questionId}")
    fun updateQuestion(
        @PathVariable questionId: Long,
        @Valid @RequestBody request: UpdateQuestionRequest,
        @UserContext userEntity: UserEntity,
    ): QuestionDto {
        return questionService.updateQuestion(questionId, request, userEntity)
    }

    @GetMapping("/{id}")
    fun getQuestion(
        @PathVariable id: Long,
    ): QuestionDto {
        return questionService.getQuestion(id)
    }
}
