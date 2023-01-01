package com.wafflestudio.team2.jisik2n.core.question

import com.wafflestudio.team2.jisik2n.core.question.api.request.CreateQuestionRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.question.service.QuestionService
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class QuestionServiceTest @Autowired constructor(
    private val questionService: QuestionService,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
) {
    @BeforeEach
    fun setup() {
        questionRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `Create Question`() {
        val createQuestionRequest = CreateQuestionRequest(
            title = "test",
            content = "test",
            photo = null,
        )
        val user = createTestUser(1)

        val question = questionService.createQuestion(createQuestionRequest, user)

        assertThat(questionRepository.findAll()).hasSize(1)
        assertThat(question.title).isEqualTo(createQuestionRequest.title)
        assertThat(question.content).isEqualTo(createQuestionRequest.content)
    }

    fun createTestUser(id: Long): UserEntity {
        val user = UserEntity(
            username = "usernameTest$id",
            password = "passwordTest$id",
            isMale = true,
            profileImage = null,
            lastLogin = LocalDateTime.now()
        )

        return userRepository.save(user)
    }
}
