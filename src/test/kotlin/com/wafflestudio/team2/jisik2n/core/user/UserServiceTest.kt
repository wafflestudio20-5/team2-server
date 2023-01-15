package com.wafflestudio.team2.jisik2n.core.user

import com.wafflestudio.team2.jisik2n.core.answer.AnswerTestHelper
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerService
import com.wafflestudio.team2.jisik2n.core.question.QuestionTestHelper
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class UserServiceTest @Autowired constructor(
    private val answerService: AnswerService,
    private val userTestHelper: UserTestHelper,
    private val questionTestHelper: QuestionTestHelper,
    private val answerTestHelper: AnswerTestHelper
) {
    @BeforeEach
    fun setup() {
        questionTestHelper.deleteAll()
        userTestHelper.deleteAll()
    }

    @Test
    fun `Get My Answer`() {
        val user1 = userTestHelper.createTestUser(1)
        val question1: QuestionEntity = questionTestHelper.createTestQuestion(1, user1)
        val answer1: AnswerEntity = answerTestHelper.createTestAnswer(1, user1, question1)
        val answerDto = answerService.getAnswersOfQuestion(question1.id)

        Assertions.assertThat(answerDto[0].id).isEqualTo(answer1.id)
        Assertions.assertThat(answerDto[0].content).isEqualTo(answer1.content)
        Assertions.assertThat(answerDto[0].username).isEqualTo(answer1.user.username)
    }
}
