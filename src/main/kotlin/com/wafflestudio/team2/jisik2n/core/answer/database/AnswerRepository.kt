package com.wafflestudio.team2.jisik2n.core.answer.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<AnswerEntity, Long>, CustomAnswerRepository

interface CustomAnswerRepository

class CustomAnswerRepositoryImpl : CustomAnswerRepository
