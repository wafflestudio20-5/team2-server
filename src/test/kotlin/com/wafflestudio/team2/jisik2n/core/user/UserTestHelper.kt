package com.wafflestudio.team2.jisik2n.core.user

import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
internal class UserTestHelper @Autowired constructor(
    private val userRepository: UserRepository,
) {
    companion object {
        var userTestNum: Long = 100000L
    }
    fun deleteAll() {
        userRepository.deleteAll()
    }

    fun createTestUser(id: Long): UserEntity {
        val user = UserEntity(
            uid = "uid$id",
            username = "usernameTest$id",
            password = "passwordTest$id",
            isMale = true,
            isActive = true,
            profileImage = null,
            lastLogin = LocalDateTime.now()
        )

        return userRepository.save(user)
    }
}
