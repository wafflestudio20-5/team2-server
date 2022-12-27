package com.wafflestudio.team2.jisik2n.core.question.database

import com.wafflestudio.team2.jisik2n.common.BaseTimeEntity
import javax.persistence.Entity

@Entity
class QuestionEntity(
    var title: String,
    var content: String,
    var like: Long,
): BaseTimeEntity() {
}