package com.wafflestudio.team2.jisik2n.core.question.api

import com.wafflestudio.team2.jisik2n.core.question.api.request.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.validation.Valid

@RestController
class QuestionController(
    private val questionService: QuestionService,
    private val userRepository: UserRepository,
) {
    // TODO: Add authentication
    final val user: UserEntity = UserEntity(username = "test", password = "test", isMale = true, profileImage = null, lastLogin = LocalDateTime.now())
    init {
        userRepository.save(user)
    }

    @GetMapping("/api/question/search")
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionService.searchQuestion()
    }

    @PostMapping("/api/question")
    fun createQuestion(
        @Valid @RequestBody request: CreateQuestionRequest,
    ): QuestionEntity {
        return questionService.createQuestion(request, user)
    }
}
