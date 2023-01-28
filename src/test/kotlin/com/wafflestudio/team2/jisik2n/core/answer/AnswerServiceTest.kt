package com.wafflestudio.team2.jisik2n.core.answer

import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerService
import com.wafflestudio.team2.jisik2n.core.question.QuestionTestHelper
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.UserTestHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
internal class AnswerServiceTest @Autowired constructor(
    private val answerService: AnswerService,
    private val answerTestHelper: AnswerTestHelper,
    private val questionTestHelper: QuestionTestHelper,
    private val userTestHelper: UserTestHelper,
) {
    @BeforeEach
    fun cleanUp() {
        answerTestHelper.deleteAll()
    }

    @Transactional
    @Test
    fun `Select answer toggle true`() {
        val user = userTestHelper.createTestUser(1)
        val user2 = userTestHelper.createTestUser(2)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)
        val answer: AnswerEntity = answerTestHelper.createTestAnswer(1, user2, question)

        assertThat(question.close).isEqualTo(false)
        assertThat(answer.selected).isEqualTo(false)

        answerService.toggleSelectAnswer(user, answer.id, true)

        assertThat(question.answers.size).isEqualTo(1)
        assertThat(question.close).isEqualTo(true)
        assertThat(answer.selected).isEqualTo(true)
    }

    @Test
    fun `Select answer toggle false`() {
        val user = userTestHelper.createTestUser(1)
        val user2 = userTestHelper.createTestUser(2)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)
        val answer: AnswerEntity = answerTestHelper.createTestAnswer(1, user2, question)

        assertThat(question.answers.size).isEqualTo(1)
        assertThat(question.close).isEqualTo(false)
        assertThat(answer.selected).isEqualTo(false)

        answerService.toggleSelectAnswer(user, answer.id, true)
        answerService.toggleSelectAnswer(user, answer.id, false)

        assertThat(question.close).isEqualTo(false)
        assertThat(answer.selected).isEqualTo(false)
    }
}
