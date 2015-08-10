package controllers

import java.io.File
import controllers.aws.SQS
import play.api.mvc._
import scala.util.Random

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("File upload in play."))
  }

  def welcome = Action {
    Ok(views.html.welcome())
  }

  def uploadFile = Action(parse.multipartFormData) { request =>

    request.body.file("fileUpload").map { video =>
      val videoFilename = video.filename
      val contentType = video.contentType.get
      val localFile = new File("/tmp/"+ videoFilename + Random.nextDouble())
      video.ref.moveTo(localFile)

      println(localFile)

      val uploadResult = aws.s3.uploadPdf(localFile.getName, localFile)
      if(uploadResult.isRight) {
        SQS.add(uploadResult.right.get)
        println(uploadResult.right.get.toString)
      }
      else
        println(uploadResult.left.get.toString)
    }.getOrElse {
      Redirect(routes.Application.welcome())
    }
    Redirect(routes.Application.welcome())
  }
}
