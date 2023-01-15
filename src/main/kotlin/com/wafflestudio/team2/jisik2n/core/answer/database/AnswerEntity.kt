package com.wafflestudio.team2.jisik2n.core.answer.database

import com.wafflestudio.team2.jisik2n.common.ContentEntityType
import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import com.wafflestudio.team2.jisik2n.core.answer.dto.AnswerResponse
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.core.user.database.UserEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.database.UserAnswerInteractionEntity
import com.wafflestudio.team2.jisik2n.core.userAnswerInteraction.service.UserAnswerInteractionService
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "answers")
class AnswerEntity(
    var content: String,

    var selected: Boolean = false,

    @Column(columnDefinition = "datetime(6)")
    var selectedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "answer", cascade = [CascadeType.ALL])
    val photos: MutableSet<PhotoEntity> = mutableSetOf(),

    @ManyToOne @JoinColumn
    val user: UserEntity,

    @ManyToOne @JoinColumn
    val question: QuestionEntity,

    @OneToMany(mappedBy = "answer")
    val userAnswerInteractions: MutableSet<UserAnswerInteractionEntity> = mutableSetOf()
) : BaseTimeEntity(), ContentEntityType {
    override fun bringPhotos() = photos

    fun toResponse(
        loginUser: UserEntity? = null,
        answerRepository: AnswerRepository,
        s3Service: S3Service,
        userAnswerInteractionService: UserAnswerInteractionService,
    ) = AnswerResponse(
        id = this.id,
        content = this.content,
        photos = this.photos // TODO: Handle photo, optimize query
            .sortedBy { it.photosOrder }
            .map { s3Service.getUrlFromFilename(it.path) },
        createdAt = this.createdAt!!,
        selected = this.selected,
        selectedAt = this.selectedAt,
        interactionCount = userAnswerInteractionService.getCountOfInteractionOfGivenAnswer(this),
        userId = this.user.id,
        username = this.user.username,
        profileImagePath = this.user.profileImage,
        userRecentAnswerDate = answerRepository // TODO: Optimize Query
            .findFirstByUserOrderByCreatedAt(this.user)!!
            .createdAt!!,
        userIsAgreed = loginUser?.let { userAnswerInteractionService.isUserAgreed(it, this) }
    )
}
