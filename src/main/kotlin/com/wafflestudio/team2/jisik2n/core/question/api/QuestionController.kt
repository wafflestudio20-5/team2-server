package com.wafflestudio.team2.jisik2n.core.question.api

import com.wafflestudio.team2.jisik2n.core.question.api.request.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class QuestionController(
    private val questionService: QuestionService,
    private val userRepository: UserRepository,
) {

    @GetMapping("/api/question/search")
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionService.searchQuestion()
    }

    @PostMapping("/api/question")
    fun createQuestion(
        @Valid @RequestBody request: CreateQuestionRequest,
    ): QuestionEntity {
        val user = userRepository.findByIdOrNull(1)!! // TODO: Authentication
        return questionService.createQuestion(request, user)
    }
}
