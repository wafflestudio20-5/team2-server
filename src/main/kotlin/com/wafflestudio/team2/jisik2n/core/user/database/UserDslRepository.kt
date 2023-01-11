package com.wafflestudio.team2.jisik2n.core.user.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.question.database.QQuestionEntity
import org.springframework.stereotype.Component
import com.wafflestudio.team2.jisik2n.core.user.database.QUserEntity.userEntity
import com.wafflestudio.team2.jisik2n.core.user.dto.GetUserQuestionResponse

@Component
class UserDslRepository(
    private val queryFactory: JPAQueryFactory
) {

    fun getUserProfile(userId: Long?): UserEntity {
        val userEntity: QUserEntity = QUserEntity.userEntity

        return queryFactory.select(userEntity).from(userEntity)
            .where(userEntity.id.eq(userId)).fetchOne()
            ?: throw Jisik2n404("해당하는 유저가 없습니다")
    }

    fun getUserQuestion(userEntity: UserEntity): GetUserQuestionResponse? {
        val user = QUserEntity.userEntity
        val question = QQuestionEntity.questionEntity

        return queryFactory.select(Projections.constructor(GetUserQuestionResponse::class.java, user.id, user.username, question))
            .from(user).where(user.eq(userEntity)).leftJoin(question).on(question.user.username.eq(user.username)).fetchOne()
    }
}
