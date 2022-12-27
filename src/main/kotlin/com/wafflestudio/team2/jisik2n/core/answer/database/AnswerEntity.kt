package com.wafflestudio.team2.jisik2n.core.answer.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import javax.persistence.Entity

@Entity
class AnswerEntity(
    var content: String,
) : BaseTimeEntity()
