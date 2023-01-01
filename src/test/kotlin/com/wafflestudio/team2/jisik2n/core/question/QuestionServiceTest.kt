package com.wafflestudio.team2.jisik2n.core.question

import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.UserTestHelper
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class QuestionServiceTest @Autowired constructor(
    private val questionService: QuestionService,
    private val questionRepository: QuestionRepository,
    private val userTestHelper: UserTestHelper,
) {
    @BeforeEach
    fun setup() {
        questionRepository.deleteAll()
        userTestHelper.deleteAll()
    }

    @Test
    fun `Create Question`() {
        val createQuestionRequest = CreateQuestionRequest(
            title = "test",
            content = "test",
            photos = listOf("test"),
        )
        val user = userTestHelper.createTestUser(1)

        val question = questionService.createQuestion(createQuestionRequest, user)

        assertThat(questionRepository.findAll()).hasSize(1)
        assertThat(question.title).isEqualTo(createQuestionRequest.title)
        assertThat(question.content).isEqualTo(createQuestionRequest.content)
    }
}
