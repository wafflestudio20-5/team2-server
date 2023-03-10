package com.wafflestudio.team2.jisik2n.core.question

import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import com.wafflestudio.team2.jisik2n.common.Jisik2n401
import com.wafflestudio.team2.jisik2n.common.SearchOrderType
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoService
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoServiceImpl
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.dto.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.dto.UpdateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionServiceImpl
import com.wafflestudio.team2.jisik2n.core.user.UserTestHelper
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
internal class QuestionServiceTest @Autowired constructor(
    private val questionService: QuestionService,
    private val photoRepository: PhotoRepository,
    private val questionRepository: QuestionRepository,
    private val questionTestHelper: QuestionTestHelper,
    private val userTestHelper: UserTestHelper,
) {
    @BeforeEach
    fun setup() {
        userTestHelper.deleteAll()
        questionTestHelper.deleteAll()
    }

    private val mockS3Service: S3Service = mockk {
        every { getFilenameFromUrl(any()) } returnsArgument 0
        every { getUrlFromFilename(any()) } returnsArgument 0
    }
    private val mockPhotoService: PhotoService = spyk(PhotoServiceImpl(photoRepository, mockS3Service))

    @Test
    fun `Get Question`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)

        val questionDto = questionService.getQuestion(question.id)

        assertThat(questionDto.id).isEqualTo(question.id)
        assertThat(questionDto.title).isEqualTo(question.title)
        assertThat(questionDto.content).isEqualTo(question.content)
        assertThat(questionDto.username).isEqualTo(question.user.username)
        assertThat(questionDto.tag.size).isEqualTo(2)
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
            tag = listOf("tag1", "tag2"),
            photos = listOf(),
        )
        val user = userTestHelper.createTestUser(1)

        val question = questionService.createQuestion(createQuestionRequest, user)

        assertThat(questionRepository.findAll()).hasSize(1)
        assertThat(question.title).isEqualTo(createQuestionRequest.title)
        assertThat(question.content).isEqualTo(createQuestionRequest.content)
        assertThat(question.tag).isEqualTo(createQuestionRequest.tag)
    }

    @Test
    fun `Create Question - Empty tag`() {
        val createQuestionRequest = CreateQuestionRequest(
            title = "test",
            content = "test",
            tag = listOf(),
            photos = listOf(),
        )
        val user = userTestHelper.createTestUser(1)

        val question = questionService.createQuestion(createQuestionRequest, user)

        assertThat(questionRepository.findAll()).hasSize(1)
        assertThat(question.title).isEqualTo(createQuestionRequest.title)
        assertThat(question.content).isEqualTo(createQuestionRequest.content)
        assertThat(question.tag.size).isEqualTo(0)
    }

    @Transactional
    @Test
    fun `Update Question`() {
        val user = userTestHelper.createTestUser(1)
        val photos = listOf("photo#1")
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user, photos)

        val mockQuestionService: QuestionService = spyk(
            objToCopy = QuestionServiceImpl(
                questionRepository = questionRepository,
                photoService = mockPhotoService,
                s3Service = mockS3Service,
            ),
            recordPrivateCalls = true,
        )

        val updateQuestionRequest = UpdateQuestionRequest(
            title = "updateTitle",
            content = "updateTitle",
            tag = listOf("updateTag1", "updateTag2"),
            photos = listOf("photo#1", "photo#2"), // TODO: s3 url valid
        )

        val questionDto = mockQuestionService.updateQuestion(question.id, updateQuestionRequest, user)

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
            tag = listOf("updateTag1", "updateTag2"),
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
            tag = listOf("updateTag1", "updateTag2"),
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

//     TODO: Regenerate test for updated logic
    @Test
    fun `Search Question`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)
        val question2: QuestionEntity = questionTestHelper.createTestQuestion(2, user)
        val question3: QuestionEntity = questionTestHelper.createTestQuestion(3, user)

        val questionDtoList = questionService.searchQuestion(order = SearchOrderType.DATE, keyword = "")

        assertThat(questionDtoList).hasSize(3)
        assertThat(questionDtoList[0].questionId).isEqualTo(question3.id)
        assertThat(questionDtoList[1].questionId).isEqualTo(question2.id)
        assertThat(questionDtoList[2].questionId).isEqualTo(question.id)
    }

    @Test
    fun `Search Question - Order by like`() {
        val user = userTestHelper.createTestUser(1)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)
        val question2: QuestionEntity = questionTestHelper.createTestQuestion(2, user)
        val question3: QuestionEntity = questionTestHelper.createTestQuestion(3, user)

        questionTestHelper.createQuestionLikeUser(question, 20)
        questionTestHelper.createQuestionLikeUser(question2, 30)
        questionTestHelper.createQuestionLikeUser(question3, 10)

        val questionDtoList = questionService.searchQuestion(order = SearchOrderType.LIKE, keyword = "")

        assertThat(questionDtoList[0].questionId).isEqualTo(question2.id)
        assertThat(questionDtoList[1].questionId).isEqualTo(question.id)
        assertThat(questionDtoList[2].questionId).isEqualTo(question3.id)
    }
}
