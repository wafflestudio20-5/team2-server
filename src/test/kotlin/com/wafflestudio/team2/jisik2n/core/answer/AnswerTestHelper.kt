package com.wafflestudio.team2.jisik2n.core.answer

import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class AnswerTestHelper @Autowired constructor(
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val photoRepository: PhotoRepository,
) {
    fun deleteAll() {
        questionRepository.deleteAll()
        answerRepository.deleteAll()
        photoRepository.deleteAll()
    }

    fun createTestAnswer(id: Long, user: UserEntity, question: QuestionEntity): AnswerEntity {
        val answer = AnswerEntity(
            content = "contentTest$id",
            user = user,
            question = question
        )
        question.answers.add(answer)

        return answerRepository.save(answer)
    }
}
