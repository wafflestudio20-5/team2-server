package com.wafflestudio.team2.jisik2n.core.userQuestionLike.database

import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import javax.persistence.*

@Entity
class UserQuestionLikeEntity(
    @ManyToOne @JoinColumn
    val user: UserEntity,

    @ManyToOne @JoinColumn
    val question: QuestionEntity,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
