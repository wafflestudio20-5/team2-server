package com.wafflestudio.team2.jisik2n.core.question.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userQuestionLike.database.UserQuestionLikeEntity
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "questions")
class QuestionEntity(
    @Column(nullable = true)
    var title: String? = null,

    var content: String,

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    val photos: MutableList<PhotoEntity> = mutableListOf(),

    @JsonIgnoreProperties("questions")
    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    val answers: MutableSet<AnswerEntity> = mutableSetOf(),

    var close: Boolean = false,

    @Column(columnDefinition = "datetime(6)", nullable = true)
    var closedAt: LocalDateTime? = null,

    @JsonIgnoreProperties("questions", "answers")
    @ManyToOne @JoinColumn
    val user: UserEntity,

    @OneToMany(mappedBy = "question")
    val userQuestionLikes: MutableSet<UserQuestionLikeEntity> = mutableSetOf(),

) : BaseTimeEntity()
