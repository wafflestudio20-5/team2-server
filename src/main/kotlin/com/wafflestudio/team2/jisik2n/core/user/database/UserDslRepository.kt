package com.wafflestudio.team2.jisik2n.core.user.database

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import com.wafflestudio.team2.jisik2n.core.question.database.QQuestionEntity
import org.springframework.stereotype.Component
import com.wafflestudio.team2.jisik2n.core.user.database.QUserEntity.userEntity

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

    fun getUserQuestion(userEntity: UserEntity): List<UserEntity> {
        val user = QUserEntity.userEntity
        val question = QQuestionEntity.questionEntity

        return queryFactory.select(Projections.constructor(userEntity::class.java, user, question.title)).from(user).leftJoin(question).fetch()
    }
}
