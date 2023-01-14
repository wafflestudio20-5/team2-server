package com.wafflestudio.team2.jisik2n.core.question

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.dto.UpdateQuestionRequest
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
            photos = listOf("test"),
        )
        val user = userTestHelper.createTestUser(1)

        val question = questionService.createQuestion(createQuestionRequest, user)

        assertThat(questionRepository.findAll()).hasSize(1)
        assertThat(question.title).isEqualTo(createQuestionRequest.title)
        assertThat(question.content).isEqualTo(createQuestionRequest.content)
    }

    @Test
    fun `Update Question`() {
        val user = userTestHelper.createTestUser(1)
        val photos = listOf("photo#1")
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user, photos)
        val updateQuestionRequest = UpdateQuestionRequest(
            title = "updateTitle",
            content = "updateTitle",
            photos = listOf("photo#2", "photo#1"),
        )

        val questionDto = questionService.updateQuestion(question.id, updateQuestionRequest, user)

        assertThat(questionDto.title).isEqualTo(updateQuestionRequest.title)
        assertThat(questionDto.content).isEqualTo(updateQuestionRequest.content)
        assertThat(questionDto.photos.size).isEqualTo(updateQuestionRequest.photos.size)
        assertThat(questionDto.photos).containsAll(updateQuestionRequest.photos)
    }

    @Test
    fun `Update Question - Wrong question number`() {
        val user = userTestHelper.createTestUser(1)
        val updateQuestionRequest = UpdateQuestionRequest(
            title = "updateTitle",
            content = "updateTitle",
            photos = listOf("photo#2", "photo#1"),
        )

        val throwable = catchThrowable { questionService.updateQuestion(1, updateQuestionRequest, user) }

        assertThat(throwable).isInstanceOf(Jisik2n400::class.java)
    }

    @Test
    fun `Update Question - Wrong user`() {
        val user = userTestHelper.createTestUser(1)
        val photos = listOf("photo#1")
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user, photos)
        val updateQuestionRequest = UpdateQuestionRequest(
            title = "updateTitle",
            content = "updateTitle",
            photos = listOf("photo#2", "photo#1"),
        )
        val user2 = userTestHelper.createTestUser(2)

        val throwable = catchThrowable { questionService.updateQuestion(question.id, updateQuestionRequest, user2) }

        assertThat(throwable).isInstanceOf(Jisik2n401::class.java)
    }

    @Test
    fun `Delete Question`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)

        questionService.deleteQuestion(question.id, user)

        assertThat(questionRepository.findAll()).hasSize(0)
    }

    @Test
    fun `Delete Question - Wrong question number`() {
        val user = userTestHelper.createTestUser(1)

        val throwable = catchThrowable { questionService.deleteQuestion(1, user) }

        assertThat(throwable).isInstanceOf(Jisik2n400::class.java)
    }

    @Test
    fun `Delete Question - Wrong user`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)
        val user2 = userTestHelper.createTestUser(2)

        val throwable = catchThrowable { questionService.deleteQuestion(question.id, user2) }

        assertThat(throwable).isInstanceOf(Jisik2n401::class.java)
    }
}
