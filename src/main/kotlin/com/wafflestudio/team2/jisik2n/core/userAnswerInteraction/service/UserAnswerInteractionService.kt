package com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.UserAnswerInteractionEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.UserAnswerInteractionRepository
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.dto.UserAnswerInteractionCountResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.transaction.Transactional

interface UserAnswerInteractionService {
    fun getCountOfInteraction(answerId: Long): UserAnswerInteractionCountResponse
    fun getCountOfInteractionOfGivenAnswer(answer: AnswerEntity): UserAnswerInteractionCountResponse
    fun putInteraction(loginUser: UserEntity, answerId: Long, isAgree: Boolean)
    fun isUserAgreed(user: UserEntity, answer: AnswerEntity): Boolean?
}

@Service
class UserAnswerInteractionServiceImpl(
    private val answerRepository: AnswerRepository,
    private val userAnswerInteractionRepository: UserAnswerInteractionRepository,
) : UserAnswerInteractionService {
    override fun getCountOfInteraction(answerId: Long): UserAnswerInteractionCountResponse {
        val answer = answerRepository.findByIdOrNull(answerId)
            ?: throw Jisik2n404("$answerId 질문이 존재하지 않습니다.")

        return getCountOfInteractionOfGivenAnswer(answer)
    }

    override fun getCountOfInteractionOfGivenAnswer(answer: AnswerEntity): UserAnswerInteractionCountResponse {
        // TODO: Query Optimize
        val agreeCnt = answer.userAnswerInteractions.count { it.isAgree }
        val disagreeCnt = answer.userAnswerInteractions.count { !it.isAgree }

        return UserAnswerInteractionCountResponse(agreeCnt, disagreeCnt)
    }

    @Transactional
    override fun putInteraction(loginUser: UserEntity, answerId: Long, isAgree: Boolean) {
        val answer = answerRepository.findByIdOrNull(answerId)
            ?: throw Jisik2n404("$answerId 질문이 존재하지 않습니다.")

        userAnswerInteractionRepository.findByUserAndAnswer(loginUser, answer)
            ?.let { // when interaction already exists
                when (it.isAgree) {
                    // when push the same button, remove the interaction
                    isAgree -> userAnswerInteractionRepository.delete(it)
                    // when push other button, change it to other interaction
                    else -> it.isAgree = !it.isAgree
                }
            } ?: let { // when interaction does not exist
            userAnswerInteractionRepository.save(
                UserAnswerInteractionEntity(
                    user = loginUser,
                    answer,
                    isAgree
                )
            )
        }
    }

    override fun isUserAgreed(user: UserEntity, answer: AnswerEntity) =
        userAnswerInteractionRepository.findByUserAndAnswer(user, answer)
            ?. isAgree
}
