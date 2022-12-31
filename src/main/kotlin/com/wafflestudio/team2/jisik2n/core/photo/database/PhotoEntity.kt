package com.wafflestudio.team2.jisik2n.core.photo.database

import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import javax.persistence.*

@Entity(name = "photos")
class PhotoEntity(
    @Column(unique = true)
    val path: String,

    @ManyToOne @JoinColumn
    var question: QuestionEntity? = null,

    @ManyToOne @JoinColumn
    var answer: AnswerEntity? = null,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
