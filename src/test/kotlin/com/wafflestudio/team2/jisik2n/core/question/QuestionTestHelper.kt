package com.wafflestudio.team2.jisik2n.core.question

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.UserTestHelper
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class QuestionTestHelper @Autowired constructor(
    private val questionRepository: QuestionRepository,
    private val photoRepository: PhotoRepository,
    private val userQuestionLikeRepository: UserQuestionLikeRepository,
    private val userTestHelper: UserTestHelper,
) {
    companion object {
        var userTestNum: Long = 100000L
    }
    fun deleteAll() {
        photoRepository.deleteAll()
        userQuestionLikeRepository.deleteAll()
        questionRepository.deleteAll()
    }

    fun createTestQuestion(id: Long, user: UserEntity, photos: List<String> = listOf()): QuestionEntity {
        val question = QuestionEntity(
            title = "titleTest$id",
            content = "contentTest$id",
            tag = "tag$id-1/tag$id-2",
            user = user,
        )

        return questionRepository.save(question)
    }

    fun createQuestionLikeUser(question: QuestionEntity, num: Long) {
        for (i in 1..num) {
            val user = userTestHelper.createTestUser(userTestNum--)
            val newUserQuestionLikeEntity = UserQuestionLikeEntity(
                user = user,
                question = question,
            )
            userQuestionLikeRepository.save(newUserQuestionLikeEntity)
            question.userQuestionLikes.add(newUserQuestionLikeEntity)
        }
    }
}
