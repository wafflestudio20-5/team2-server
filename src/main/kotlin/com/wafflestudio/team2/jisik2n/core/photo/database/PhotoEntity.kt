package com.wafflestudio.team2.jisik2n.core.photo.database

import javax.persistence.*

@Entity
class PhotoEntity(
    val path: String,
    val position: Int,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
