package com.wafflestudio.team2.jisik2n.core.photo.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhotoRepository : JpaRepository<PhotoEntity, Long>
