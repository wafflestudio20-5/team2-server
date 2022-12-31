package com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database

import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import javax.persistence.*

@Entity(name="userAnswerInteraction")
class UserAnswerInteractionEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne @JoinColumn
    val user: UserEntity,

    @ManyToOne @JoinColumn
    val answer: AnswerEntity,

    val isAgree: Boolean
)
