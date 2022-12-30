package com.wafflestudio.team2.jisik2n.core.answer.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerResponse
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.UserAnswerInteractionEntity
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

    @OneToMany(cascade = [CascadeType.ALL]) @JoinColumn
    val photos: MutableSet<PhotoEntity> = mutableSetOf(),

    @ManyToOne @JoinColumn
    val user: UserEntity,

    @ManyToOne @JoinColumn
    val question: QuestionEntity,

    @OneToMany(mappedBy = "answer")
    val userAnswerInteractions: MutableSet<UserAnswerInteractionEntity> = mutableSetOf(),
) : BaseTimeEntity() {
    fun toResponse(answerRepository: AnswerRepository) = AnswerResponse(
        content = this.content,
        selected = this.selected,
        selectedAt = this.selectedAt,
        photos = this.photos // TODO: Handle photo, optimize query
            .sortedBy { it.position }
            .map { it.path },
        username = this.user.username,
        profileImagePath = this.user.profileImage,
        userRecentAnswerDate = answerRepository // TODO: Optimize Query
            .findFirstByUserOrderByCreatedAt(this.user)!!
            .createdAt!!,
    )
}
