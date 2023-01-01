package com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.service

import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerRepository
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.dto.UserAnswerInteractionCountResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

interface UserAnswerInteractionService {
    fun getCountOfInteraction(answerId: Long): UserAnswerInteractionCountResponse
}

@Service
class UserAnswerInteractionServiceImpl(
    private val answerRepository: AnswerRepository,
) : UserAnswerInteractionService {
    override fun getCountOfInteraction(answerId: Long): UserAnswerInteractionCountResponse {
        val answer = answerRepository.findByIdOrNull(answerId)
            ?: throw Jisik2n404("$answerId 질문이 존재하지 않습니다.")

        // TODO: Query Optimize
        val agreeCnt = answer.userAnswerInteractions.count { it.isAgree }
        val disagreeCnt = answer.userAnswerInteractions.count { !it.isAgree }

        return UserAnswerInteractionCountResponse(agreeCnt, disagreeCnt)
    }
}
