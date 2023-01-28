package com.wafflestudio.team2.jisik2n.core.answer

import com.wafflestudio.team2.jisik2n.common.Jisik2n403
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerRequest
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerService
import com.wafflestudio.team2.jisik2n.core.answer.service.AnswerServiceImpl
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoService
import com.wafflestudio.team2.jisik2n.core.photo.service.PhotoServiceImpl
import com.wafflestudio.team2.jisik2n.core.question.QuestionTestHelper
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.UserTestHelper
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.UserAnswerInteractionRepository
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.assertj.core.api.Assertions
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
    private val photoRepository: PhotoRepository,
    private val answerRepository: AnswerRepository,
    private val userAnswerInteractionRepository: UserAnswerInteractionRepository,
    private val questionRepository: QuestionRepository,
) {
    private val mockS3Service: S3Service = mockk {
        every { getFilenameFromUrl(any()) } returnsArgument 0
        every { getUrlFromFilename(any()) } returnsArgument 0
    }
    private val mockPhotoService: PhotoService = spyk(PhotoServiceImpl(photoRepository, mockS3Service))
    private val mockAnswerService: AnswerService = spyk(
        objToCopy = AnswerServiceImpl(
            answerRepository = answerRepository,
            questionRepository = questionRepository,
            userAnswerInteractionRepository = userAnswerInteractionRepository,
            photoService = mockPhotoService,
        ),
        recordPrivateCalls = true,
    )

    @BeforeEach
    fun cleanUp() {
        answerTestHelper.deleteAll()
    }

    @Transactional
    @Test
    fun `Create answer`() {
        val user = userTestHelper.createTestUser(1)
        val user2 = userTestHelper.createTestUser(2)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)

        val createAnswerRequest = AnswerRequest("content", listOf("photo#1", "photo#2"))

        mockAnswerService.createAnswer(user2, question.id, createAnswerRequest)

        assertThat(question.answers.size).isEqualTo(1)
    }

    @Transactional
    @Test
    fun `Select answer - toggle true`() {
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

    @Transactional
    @Test
    fun `Select answer - toggle false`() {
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

    @Transactional
    @Test
    fun `Remove answer`() {
        val user = userTestHelper.createTestUser(1)
        val user2 = userTestHelper.createTestUser(2)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)
        val answer: AnswerEntity = answerTestHelper.createTestAnswer(1, user2, question)

        assertThat(question.answers.size).isEqualTo(1)

        answerService.removeAnswer(user2, answer.id)

        assertThat(question.answers.size).isEqualTo(0)
    }

    @Transactional
    @Test
    fun `Remove answer - Wrong User`() {
        val user = userTestHelper.createTestUser(1)
        val user2 = userTestHelper.createTestUser(2)
        val question: QuestionEntity = questionTestHelper.createTestQuestion(1, user)
        val answer: AnswerEntity = answerTestHelper.createTestAnswer(1, user2, question)

        assertThat(question.answers.size).isEqualTo(1)

        val throwable = Assertions.catchThrowable { answerService.removeAnswer(user, answer.id) }

        assertThat(throwable).isInstanceOf(Jisik2n403::class.java)
    }
}
