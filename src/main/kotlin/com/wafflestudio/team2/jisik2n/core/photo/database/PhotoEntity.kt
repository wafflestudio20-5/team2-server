package com.wafflestudio.team2.jisik2n.core.photo.database

import javax.persistence.*

@Entity(name="photos")
class PhotoEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column
    val path: String,
)
