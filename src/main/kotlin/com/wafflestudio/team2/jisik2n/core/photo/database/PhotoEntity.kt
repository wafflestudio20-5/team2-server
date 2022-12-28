package com.wafflestudio.team2.jisik2n.core.photo.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class PhotoEntity(
    @Column
    val path: String,
) : BaseTimeEntity()
