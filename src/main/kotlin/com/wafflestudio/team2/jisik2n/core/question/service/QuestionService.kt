package com.wafflestudio.team2.jisik2n.core.question.service

import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository
) {
    fun searchQuestion(): MutableList<QuestionEntity> {
        return questionRepository.findAll()
    }
}
