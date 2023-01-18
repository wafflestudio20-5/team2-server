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

    @Test
    fun `Get Like Question - should return empty list when user has no like`() {
        val user = userTestHelper.createTestUser(1)
        val result = userQuestionLikeService.getLikeQuestion(user)
        assertThat(result).isEmpty()
    }

    @Test
    fun `Get Like Question - should return list of liked questions`() {
        val user = userTestHelper.createTestUser(1)
        val question1 = questionTestHelper.createTestQuestion(1, user)
        val question2 = questionTestHelper.createTestQuestion(2, user)

        userQuestionLikeService.putLike(user, question1.id)
        userQuestionLikeService.putLike(user, question2.id)

        val result = userQuestionLikeService.getLikeQuestion(user)
        assertThat(result).hasSize(2)
    }

    @Transactional
    @Test
    fun `Like Question`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)

        val resultEntity = userQuestionLikeService.putLike(user, question.id)

        val userQuestionLikeEntity = userQuestionLikeRepository.findByQuestionAndUser(question, user)
        assertThat(userQuestionLikeEntity).isNotNull
        assertThat(user.userQuestionLikes).contains(userQuestionLikeEntity)
        assertThat(question.userQuestionLikes).contains(userQuestionLikeEntity)

        assertThat(resultEntity.id).isEqualTo(userQuestionLikeEntity?.id)
        assertThat(resultEntity.question.id).isEqualTo(userQuestionLikeEntity?.question?.id)
        assertThat(resultEntity.userId).isEqualTo(userQuestionLikeEntity?.user?.id)
    }
}
