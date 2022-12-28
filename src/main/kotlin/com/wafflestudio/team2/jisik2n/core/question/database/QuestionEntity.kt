package com.wafflestudio.team2.jisik2n.core.question.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class QuestionEntity(
    @Column(nullable = true)
    var title: String? = null,

    var content: String,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn
    val photos: MutableSet<PhotoEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    val answers: MutableSet<AnswerEntity> = mutableSetOf(),

    var close: Boolean = false,

    @Column(columnDefinition = "datetime(6)", nullable = true)
    var closedAt: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn
    val user: UserEntity,

    // TODO: Add likes

) : BaseTimeEntity()
