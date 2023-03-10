package com.wafflestudio.team2.jisik2n.core.question.database

import com.wafflestudio.team2.jisik2n.common.ContentEntityType
import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "questions")
class QuestionEntity(
    @Column(nullable = true)
    var title: String? = null,

    @Column(columnDefinition = "text")
    var content: String,

    // "/" 으로 나누어서 구분
    var tag: String,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    val photos: MutableSet<PhotoEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    val answers: MutableSet<AnswerEntity> = mutableSetOf(),

    @Column
    var answerCount: Long = 0,

    var close: Boolean = false,

    @Column(columnDefinition = "datetime(6)", nullable = true)
    var closedAt: LocalDateTime? = null,

    @ManyToOne @JoinColumn
    val user: UserEntity,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    val userQuestionLikes: MutableSet<UserQuestionLikeEntity> = mutableSetOf(),

    var likeCount: Long = 0

) : BaseTimeEntity(), ContentEntityType {
    override fun bringPhotos() = photos
}
