package com.wafflestudio.team2.jisik2n.core.photo.api

import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/photo")
class PhotoController(
    private val s3Service: S3Service,
) {
    @PostMapping(consumes = ["multipart/form-data"])
    fun uploadPhoto(@RequestParam image: MultipartFile): String {
        return s3Service.upload(image)
    }
}
