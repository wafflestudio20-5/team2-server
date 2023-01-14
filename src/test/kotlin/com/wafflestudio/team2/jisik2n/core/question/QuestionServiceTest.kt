package com.wafflestudio.team2.jisik2n.core.question

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.UserTestHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class QuestionServiceTest @Autowired constructor(
    private val questionService: QuestionService,
    private val questionRepository: QuestionRepository,
    private val questionTestHelper: QuestionTestHelper,
    private val userTestHelper: UserTestHelper,
) {
    @BeforeEach
    fun setup() {
        questionTestHelper.deleteAll()
        userTestHelper.deleteAll()
    }

    @Test
    fun `Get Question`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)

        val questionDto = questionService.getQuestion(question.id)

        assertThat(questionDto.id).isEqualTo(question.id)
        assertThat(questionDto.title).isEqualTo(question.title)
        assertThat(questionDto.content).isEqualTo(question.content)
        assertThat(questionDto.username).isEqualTo(question.user.username)
    }

    @Test
    fun `Get Question - Wrong question number`() {
        val throwable = catchThrowable { questionService.getQuestion(1) }

        assertThat(throwable).isInstanceOf(Jisik2n400::class.java)
    }

    @Test
    fun `Create Question`() {
        val createQuestionRequest = CreateQuestionRequest(
            title = "test",
            content = "test",
            photos = listOf(),
        )
        val user = userTestHelper.createTestUser(1)

        val question = questionService.createQuestion(createQuestionRequest, user)

        assertThat(questionRepository.findAll()).hasSize(1)
        assertThat(question.title).isEqualTo(createQuestionRequest.title)
        assertThat(question.content).isEqualTo(createQuestionRequest.content)
    }
}
