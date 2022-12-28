package com.wafflestudio.team2.jisik2n.core.answer.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class AnswerEntity(
    var content: String,

    var selected: Boolean = false,

    @Column(columnDefinition = "datetime(6)")
    var selectedAt: LocalDateTime? = null,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn
    val photos: MutableSet<PhotoEntity> = mutableSetOf(),

    @ManyToOne
    @JoinColumn
    val user: UserEntity,

    @ManyToOne
    @JoinColumn
    val question: QuestionEntity,

    // TODO: Add agree and disagree
) : BaseTimeEntity()
