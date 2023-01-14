package com.wafflestudio.team2.jisik2n.core.question

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class QuestionTestHelper @Autowired constructor(
    private val questionRepository: QuestionRepository,
    private val photoRepository: PhotoRepository,
) {
    fun deleteAll() {
        questionRepository.deleteAll()
        photoRepository.deleteAll()
    }

    fun createTestQuestion(id: Long, user: UserEntity, photos: List<String> = listOf()): QuestionEntity {
        val question = QuestionEntity(
            title = "titleTest$id",
            content = "contentTest$id",
            user = user,
        )

        return questionRepository.save(question)
    }
}
