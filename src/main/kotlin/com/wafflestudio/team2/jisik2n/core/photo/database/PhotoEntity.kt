package com.wafflestudio.team2.jisik2n.core.photo.database

import javax.persistence.*

@Entity(name = "photos")
class PhotoEntity(
    @Column
    val path: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
