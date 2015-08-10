package controllers.aws

import java.io.File
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{PutObjectRequest, CannedAccessControlList}
import com.typesafe.config.ConfigFactory

/**
 * Created by pdesai on 8/2/15.
 */
object s3 {
  var amazonS3Client: AmazonS3Client = null
  var pdfBucket: String = null
  var ebooksBucket: String = null
  
  // We are not catching exceptions here. If init fails, we want to know about it
  def init(): Unit = {
    var conf = ConfigFactory.load
    val yourAWSCredentials = new BasicAWSCredentials(conf.getString("aws.accesskey"), conf.getString("aws.secretKey"))
    pdfBucket = conf.getString("bucket.pdf")
    ebooksBucket = conf.getString("bucket.ebooks")
    amazonS3Client = new AmazonS3Client(yourAWSCredentials)
  }

  def uploadPdf(fileName: String, fileToUpload: File): Either[String, String] = {
    uploadToS3(pdfBucket, fileName, fileToUpload)
  }

  def uploadEbook(fileName: String, fileToUpload: File): Either[String, String] = {
    uploadToS3(ebooksBucket, fileName, fileToUpload)
  }

  private def uploadToS3(bucketName: String, fileName: String, fileToUpload: File): Either[String, String] = {
    try {
      val url = amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, fileToUpload).withCannedAcl(CannedAccessControlList.PublicRead))
      Right(amazonS3Client.getResourceUrl(bucketName, fileName))
    }
    catch {
      case e: Exception =>
        Left(s"Could not upload file $fileName to S3: ${e.toString}")
    }
  }
}
