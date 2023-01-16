package com.wafflestudio.team2.jisik2n.core.userQuestionLike.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.common.UserContext
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.dto.UserQuestionLikeDto
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.service.UserQuestionLikeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/questionLike")
@RestController
class UserQuestionLikeController(
    private val userQuestionLikeService: UserQuestionLikeService,
) {
    @Authenticated
    @GetMapping("/")
    fun likeQuestion(
        @UserContext userEntity: UserEntity,
    ): List<UserQuestionLikeDto> {
        return userQuestionLikeService.getLikeQuestion(userEntity)
    }

    @Authenticated
    @PutMapping("/{questionId}/like")
    fun putLike(
        @UserContext userEntity: UserEntity,
        @PathVariable questionId: Long,
    ): UserQuestionLikeDto {
        return userQuestionLikeService.putLike(userEntity, questionId)
    }
}
