package com.wafflestudio.team2.jisik2n.core.photo.api

import com.wafflestudio.team2.jisik2n.common.Authenticated
import com.wafflestudio.team2.jisik2n.external.s3.service.S3Service
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/photo")
class PhotoController(
    private val s3Service: S3Service,
) {
    @Authenticated
    @PostMapping(consumes = ["multipart/form-data"])
    fun uploadPhoto(@RequestParam image: MultipartFile): String {
        return s3Service.upload(image)
    }

    @Authenticated
    @DeleteMapping
    fun revertUploadedPhoto(@RequestParam(name = "url") url: String): ResponseEntity<String> {
        s3Service.deleteWithUrl(url)
        return ResponseEntity<String>(url, HttpStatus.OK)
    }
}
