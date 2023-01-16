package com.wafflestudio.team2.jisik2n.core.userQuestionLike

import com.wafflestudio.team2.jisik2n.core.question.QuestionTestHelper
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.UserTestHelper
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeRepository
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.service.UserQuestionLikeService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.assertThat
// import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
internal class UserQuestionLikeServiceTest @Autowired constructor(
    private val userQuestionLikeService: UserQuestionLikeService,
    private val userQuestionLikeRepository: UserQuestionLikeRepository,
    private val questionTestHelper: QuestionTestHelper,
    private val userTestHelper: UserTestHelper,
) {
    @BeforeEach
    fun setup() {
        userTestHelper.deleteAll()
        questionTestHelper.deleteAll()
    }

    @Transactional
    @Test
    fun `Like Question`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)

        userQuestionLikeService.putLike(user, question.id)

        val userQuestionLikeEntity = userQuestionLikeRepository.findByQuestionAndUser(question, user)
        assertThat(userQuestionLikeEntity).isNotNull
        assertThat(user.userQuestionLikes).contains(userQuestionLikeEntity)
        assertThat(question.userQuestionLikes).contains(userQuestionLikeEntity)
    }
}
