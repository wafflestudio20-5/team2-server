package com.wafflestudio.team2.jisik2n.core.user.database

import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.team2.jisik2n.common.Jisik2n404
import org.springframework.stereotype.Component

@Component
class UserDslRepository(
    private val queryFactory: JPAQueryFactory,

) {

    fun getUserProfile(userEntity: Long?): UserEntity {
        val userEntity: QUserEntity = QUserEntity.userEntity

        return queryFactory.select(userEntity).from(userEntity)
            .where(userEntity.eq(userEntity)).fetchOne()
            ?: throw Jisik2n404("해당하는 유저가 없습니다")
    }
//
//    fun getUserQuestion(userEntity: UserEntity): GetUserQuestionResponse? {
//        val user = QUserEntity.userEntity
//        val question =
//
//        return queryFactory.select(Projections.constructor(GetUserQuestionResponse::class.java, user.id, user.username, question))
//            .from(user).where(user.eq(userEntity)).leftJoin(question).on(question.user.username.eq(user.username)).fetchOne()
//    }
}
