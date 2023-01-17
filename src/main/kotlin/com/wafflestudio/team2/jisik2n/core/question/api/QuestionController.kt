package com.wafflestudio.team2.jisik2n.core.question.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.SEARCH_ORDER
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.dto.QuestionDto
import com.wafflestudio.team2.jisik2n.core.question.dto.UpdateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        @RequestParam(required = false) amount: Long = 20,
        @RequestParam(required = false) pageNum: Long = 0,
        @RequestParam(required = false, defaultValue = "") keyword: String,
    ) = try {
        val orderEnum = SEARCH_ORDER.valueOf(order)
        val isClosedBoolean = when (isClosed) {
            "true" -> true
            "false" -> false
            "null" -> null
            else -> throw Jisik2n400("isClosed 의 값이 잘못되었습니다.")
        }
        questionService.searchQuestion(orderEnum, isClosedBoolean, keyword, amount, pageNum)
    } catch (e: IllegalArgumentException) {
        throw Jisik2n400("order 의 값이 잘못되었습니다.")
    }

    @Authenticated
    @PostMapping("/")
    fun createQuestion(
        @Valid @RequestBody request: CreateQuestionRequest,
        @UserContext userEntity: UserEntity,
    ): QuestionDto {
        return questionService.createQuestion(request, userEntity)
    }

    @Authenticated
    @PutMapping("/{questionId}")
    fun updateQuestion(
        @PathVariable questionId: Long,
        @Valid @RequestBody request: UpdateQuestionRequest,
        @UserContext userEntity: UserEntity,
    ): QuestionDto {
        return questionService.updateQuestion(questionId, request, userEntity)
    }

    @Authenticated
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(
        @PathVariable questionId: Long,
        @UserContext userEntity: UserEntity,
    ): ResponseEntity<String> {
        questionService.deleteQuestion(questionId, userEntity)
        return ResponseEntity<String>("$questionId", HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getQuestion(
        @PathVariable id: Long,
    ): QuestionDto {
        return questionService.getQuestion(id)
    }
}
