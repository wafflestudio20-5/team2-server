package com.wafflestudio.team2.jisik2n.core.question.database

import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository : JpaRepository<QuestionEntity, Long>
