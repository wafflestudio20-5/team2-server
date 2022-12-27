package com.wafflestudio.team2.jisik2n.core.user.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import javax.persistence.*

@Entity
class UserEntity(
    var uid: String,
    var username: String,
    var password: String,
) : BaseTimeEntity() {

}