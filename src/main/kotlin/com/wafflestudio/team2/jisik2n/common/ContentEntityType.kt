package com.wafflestudio.team2.jisik2n.common

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity

interface ContentEntityType {
    fun bringPhotos(): MutableSet<PhotoEntity>
}
