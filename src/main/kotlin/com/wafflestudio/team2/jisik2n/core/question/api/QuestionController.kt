package com.wafflestudio.team2.jisik2n.core.question.api

import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class QuestionController(
    private val questionService: QuestionService,
) {
    @GetMapping("/api/question/search")
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionService.searchQuestion()
    }
}
