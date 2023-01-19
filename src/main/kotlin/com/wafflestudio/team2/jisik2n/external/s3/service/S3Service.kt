package com.wafflestudio.team2.jisik2n.external.s3.service

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.wafflestudio.team2.jisik2n.common.Jisik2n400
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import java.util.UUID

interface S3Service {
    /**
     * Upload the multipart file to given dir, and return url
     */
    fun upload(
        multipartFile: MultipartFile,
        dir: String? = null,
        setUUIDFilename: Boolean = true
    ): String

    fun deleteWithUrl(url: String)

    fun deleteWithPath(path: String)

    fun getFilenameFromUrl(url: String): String
    fun getUrlFromFilename(filename: String): String
}

// Implemented with not saving the temporary files
@Service
class S3ServiceImpl(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket-name}")
    private val bucket: String,
    @Value("\${cloud.aws.s3.dir}")
    private val dir: String,
) : S3Service {
    override fun upload(
        multipartFile: MultipartFile,
        dir: String?,
        setUUIDFilename: Boolean,
    ): String {
        val filename = (dir ?: this.dir) + "/" +
            if (setUUIDFilename) {
                createUUIDFilename(multipartFile.originalFilename!!)
            } else {
                multipartFile.originalFilename
            }
        return amazonS3Client.run { // Upload file and get url
            putObject(
                PutObjectRequest(
                    bucket,
                    filename,
                    BufferedInputStream(multipartFile.inputStream), // TODO: Maybe consider IOException
                    ObjectMetadata()
                ).withCannedAcl(CannedAccessControlList.PublicRead)
            )
            getUrl(bucket, filename).toString()
        }
    }

    override fun deleteWithUrl(url: String) = amazonS3Client.deleteObject( // TODO: Maybe consider exception
        DeleteObjectRequest(bucket, getFilenameFromUrl(url))
    )

    override fun deleteWithPath(path: String) = deleteWithUrl(getUrlFromFilename(path))

    override fun getFilenameFromUrl(url: String): String {
        val filename = url.substringAfter("com/")
        try { // check if given url is valid
            assert(amazonS3Client.getUrl(bucket, filename).toString() == url)
        } catch (e: AssertionError) {
            throw Jisik2n400("사진 url이 잘못되었습니다.")
        }
        // TODO: catch AssertionError
        return filename
    }

    override fun getUrlFromFilename(filename: String) = amazonS3Client.getUrl(bucket, filename).toString()

    private fun createUUIDFilename(filename: String): String {
        val ext = filename.substringAfterLast('.')
        return UUID.randomUUID().toString() + "." + ext
    }

    /**
     * S3 services with saving temp files in server
     * Leave this just in case
     */
    // fun upload(multipartFile: MultipartFile, dir: String = this.dir): String {
    //     val uploadFile = convert(multipartFile)
    //         ?: TODO("Throw IllegalArgumentException")
    //     return upload(uploadFile, dir)
    // }
    //
    // private fun convert(multipartFile: MultipartFile): File? {
    //     val convertFile = File(multipartFile.originalFilename ?: return null)
    //     return if (convertFile.createNewFile()) {
    //             val fos = FileOutputStream(convertFile) // TODO: Handle IOException
    //             fos.write(multipartFile.bytes)
    //             convertFile
    //         } else {
    //             null
    //         }
    // }
    //
    // private fun upload(uploadFile: File, dir: String): String {
    //     val filename: String = dir + "/" + uploadFile.name
    //     val uploadImageUrl = amazonS3Client.run { // Upload file and get url
    //             putObject(
    //                 PutObjectRequest(bucket, filename, uploadFile)
    //                     .withCannedAcl(CannedAccessControlList.PublicRead)
    //             )
    //             getUrl(bucket, filename).toString()
    //         }
    //     uploadFile.delete() // Remove file from local TODO: Handle IOException
    //     return uploadImageUrl
    // }
}
