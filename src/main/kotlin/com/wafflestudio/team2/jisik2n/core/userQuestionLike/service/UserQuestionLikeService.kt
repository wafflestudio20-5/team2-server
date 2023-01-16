package com.wafflestudio.team2.jisik2n.core.userQuestionLike.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserQuestionLikeService {
    fun putLike(user: UserEntity, questionId: Long)
}

@Service
class UserQuestionLikeServiceImpl(
    private val userQuestionLikeRepository: UserQuestionLikeRepository,
    private val questionRepository: QuestionRepository,
) : UserQuestionLikeService {
    @Transactional
    override fun putLike(user: UserEntity, questionId: Long) {
        val question = questionRepository.findByIdOrNull(questionId)
            ?: throw Jisik2n404("존재하지 않는 질문 번호 입니다.(questionId: $questionId)")

        val userQuestionLikeEntity = userQuestionLikeRepository.findByQuestionAndUser(question, user)
        if (userQuestionLikeEntity != null) {
            userQuestionLikeRepository.delete(userQuestionLikeEntity)
        } else {
            userQuestionLikeRepository.save(
                UserQuestionLikeEntity(
                    user = user,
                    question = question,
                )
            )
        }
    }
}
