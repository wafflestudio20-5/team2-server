package com.wafflestudio.team2.jisik2n.core.photo.service

import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import org.springframework.stereotype.Service

@Service
class PhotoService(
    private val photoRepository: PhotoRepository
) {
    fun createPhoto(path: String): PhotoEntity {
        val newPhoto = PhotoEntity(path = path)
        photoRepository.save(newPhoto)

        return newPhoto
    }
}