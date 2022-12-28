package com.wafflestudio.team2.jisik2n.core.user.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class UserEntity(
    @Column(unique = true, nullable = true)
    val uid: String? = null,
    @Column(unique = true, nullable = true)
    val snsId: String? = null,
    @Column(unique = true)
    var username: String,
    var password: String,
    @Column(columnDefinition = "datetime(6) default '1999-01-01'")
    var lastLogin: LocalDateTime,
    var isMale: Boolean,
    @Column(nullable = true)
    var profileImage: String?,
) : BaseTimeEntity()
