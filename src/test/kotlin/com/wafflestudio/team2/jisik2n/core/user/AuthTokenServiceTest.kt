package com.wafflestudio.team2.jisik2n.core.user

import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.answer.AnswerTestHelper
import com.wafflestudio.team2.jisik2n.core.user.service.AuthTokenService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
internal class AuthTokenServiceTest @Autowired constructor(
    private val authTokenService: AuthTokenService,
    private val answerTestHelper: AnswerTestHelper,
    private val userTestHelper: UserTestHelper,
) {
    @BeforeEach
    fun setup() {
        userTestHelper.deleteAll()
        answerTestHelper.deleteAll()
    }

    @Test
    fun `Verify token`() {
        val accessToken = authTokenService.generateAccessTokenByUid("id#1")
        val refreshToken = authTokenService.generateAccessTokenByUid("id#2")

        val accessResult: Boolean? = authTokenService.verifyToken(accessToken)
        val refreshResult: Boolean? = authTokenService.verifyToken(refreshToken)

        assertThat(accessResult).isEqualTo(true)
        assertThat(refreshResult).isEqualTo(true)
    }

    @Test
    fun `Get current user id`() {
        val user = userTestHelper.createTestUser(1)
        val token = authTokenService.generateAccessTokenByUid(user.uid!!)

        val userId = authTokenService.getCurrentUserId(token)

        assertThat(userId).isEqualTo(user.id)
    }

    @Transactional
    @Test
    fun `Get current user id - wrong id`() {
        userTestHelper.createTestUser(1)
        val token = authTokenService.generateAccessTokenByUid("wrong")

        val throwable = Assertions.catchThrowable { authTokenService.getCurrentUserId(token) }

        assertThat(throwable).isInstanceOf(Jisik2n404::class.java)
    }

    @Transactional
    @Test
    fun `Get current user Uid`() {
        val user = userTestHelper.createTestUser(1)
        val token = authTokenService.generateAccessTokenByUid(user.uid!!)

        val uid = authTokenService.getCurrentUid(token)

        assertThat(uid).isEqualTo(user.uid)
    }
}
