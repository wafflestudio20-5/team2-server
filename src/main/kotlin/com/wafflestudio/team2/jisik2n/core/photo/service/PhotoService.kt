package com.wafflestudio.team2.jisik2n.core.photo.service

import com.wafflestudio.team2.jisik2n.common.ContentEntityType
import com.wafflestudio.team2.jisik2n.core.answer.database.AnswerEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoEntity
import com.wafflestudio.team2.jisik2n.core.photo.database.PhotoRepository
import com.wafflestudio.team2.jisik2n.core.photo.dto.PhotoRequest
import com.wafflestudio.team2.jisik2n.core.question.database.QuestionEntity
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import org.springframework.stereotype.Service
import java.lang.invoke.WrongMethodTypeException
import javax.transaction.Transactional

interface PhotoService {
    fun initiallyAddPhotos(contentEntity: ContentEntityType, requests: List<PhotoRequest>)
    fun modifyPhotos(contentEntity: ContentEntityType, requests: List<PhotoRequest>)
    fun deletePhotos(photos: Collection<PhotoEntity>)
}

@Service
class PhotoServiceImpl(
    private val photoRepository: PhotoRepository,
    private val s3Service: S3Service,
) : PhotoService {
    @Transactional
    override fun initiallyAddPhotos(contentEntity: ContentEntityType, requests: List<PhotoRequest>) {
        requests.map {
            val path = s3Service.getFilenameFromUrl(it.url)
            val photo = PhotoEntity(
                path,
                photosOrder = it.position!!,
            )
            connectPhotoToContent(contentEntity, photo)
            photo
        }.let {
            photoRepository.saveAll(it)
        }.let {
            contentEntity.bringPhotos().addAll(it)
        }
    }

    @Transactional
    override fun modifyPhotos(contentEntity: ContentEntityType, requests: List<PhotoRequest>) {
        // Photos saved in answer / question
        val photos = contentEntity.bringPhotos()

        requests.forEach { req ->
            val path = s3Service.getFilenameFromUrl(req.url)
            if (req.position == -1) { // Delete Photo when given position is -1
                photos.find { it.path == path }
                    ?.also {
                        photos.remove(it)
                        s3Service.deleteWithUrl(req.url)
                    }
                    .let { photoRepository.delete(it!!) }
                return@forEach
            }
            photos.find { it.path == path }
                ?.let { it.photosOrder = req.position!! } // when photo already exists, only change photoOrder
                ?: PhotoEntity( // when photo does not exists, create new photo entity
                    path,
                    photosOrder = req.position!!,
                ).also { // connect to according content
                    connectPhotoToContent(contentEntity, it)
                }.let { // add to content's photos
                    photos.add(it)
                }
        }

        photoRepository.saveAll(photos)
    }

    @Transactional
    override fun deletePhotos(photos: Collection<PhotoEntity>) {
        photos.forEach {
            s3Service.deleteWithPath(it.path)
        }
        photoRepository.deleteAll(photos)
    }

    fun connectPhotoToContent(contentEntity: ContentEntityType, photo: PhotoEntity) {
        when (contentEntity) { // Add answer/question to photo entities
            is AnswerEntity -> {
                photo.answer = contentEntity
            }

            is QuestionEntity -> {
                photo.question = contentEntity
            }

            else -> {
                throw WrongMethodTypeException("Can be used with only AnswerEntity and QuestionEntity")
            }
        }
    }
}
