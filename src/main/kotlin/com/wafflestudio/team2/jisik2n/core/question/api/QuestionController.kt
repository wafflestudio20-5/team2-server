package com.wafflestudio.team2.jisik2n.core.question.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class QuestionController(
    private val questionService: QuestionService,
) {
    @GetMapping("/api/question/search")
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionService.searchQuestion()
    }

    @Authenticated
    @PostMapping("/api/question")
    fun createQuestion(
        @Valid @RequestBody request: CreateQuestionRequest,
        @UserContext userEntity: UserEntity,
    ): QuestionEntity {
        return questionService.createQuestion(request, userEntity)
    }

    @GetMapping("/api/question/{id}")
    fun getQuestion(
        @PathVariable id: Long,
    ): QuestionEntity {
        return questionService.getQuestion(id)
    }
}
