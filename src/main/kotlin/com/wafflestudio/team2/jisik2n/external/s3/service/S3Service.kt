package com.wafflestudio.team2.jisik2n.external.s3.service

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream

interface S3Service {
    /**
     * Upload the multipart file to given dir, and return url
     */
    fun upload(multipartFile: MultipartFile, dir: String? = null): String
}

@Service
class S3ServiceImpl(
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket-name")
    private val bucket: String,
    @Value("\${cloud.aws.s3.dir")
    private val dir: String,
) : S3Service {
    override fun upload(multipartFile: MultipartFile, dir: String?): String {
        val filename = (dir ?: this.dir) + "/" + multipartFile.originalFilename
        return amazonS3Client.run { // Upload file and get url
            putObject(
                PutObjectRequest(
                    bucket,
                    filename,
                    BufferedInputStream(multipartFile.inputStream), // TODO: IOException
                    ObjectMetadata()
                ).withCannedAcl(CannedAccessControlList.PublicRead)
            )
            getUrl(bucket, filename).toString()
        }
    }

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
