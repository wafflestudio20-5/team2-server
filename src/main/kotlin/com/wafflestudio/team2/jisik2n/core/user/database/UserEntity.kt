package com.wafflestudio.team2.jisik2n.core.user.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.SignupRequest
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.UserAnswerInteractionEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity(name = "users")
class UserEntity(
    @Column(unique = true, nullable = true)
    val uid: String? = null,

    @Column(unique = true, nullable = true)
    val snsId: String? = null,

    @Column(unique = true)
    var username: String,

    var password: String?,

    @Column(columnDefinition = "datetime(6) default '1999-01-01'", nullable = true)
    var lastLogin: LocalDateTime?,

    var isMale: Boolean?,

    @Column(nullable = true)
    var profileImage: String?,

    var isActive: Boolean?,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val questions: MutableSet<QuestionEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "user")
    val userQuestionLikes: MutableSet<UserQuestionLikeEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val answers: MutableSet<AnswerEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "user")
    val userAnswerInteractions: MutableSet<UserAnswerInteractionEntity> = mutableSetOf(),

) : BaseTimeEntity() {

    companion object {
        fun signup(request: SignupRequest, encodedPassword: String): UserEntity {
            request.run {
                return UserEntity(
                    uid = request.uid,
                    snsId = null,
                    username = request.username,
                    password = encodedPassword,
                    lastLogin = null,
                    isMale = request.isMale,
                    profileImage = null,
                    isActive = true,
                    questions = mutableSetOf(),
                    answers = mutableSetOf()
                )
            }
        }
    }
}
