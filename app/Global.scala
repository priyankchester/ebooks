/**
 * Created by pdesai on 8/2/15.
 */

import controllers.aws.{SQS, s3}
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    s3.init()
    SQS.init()
    Logger.info("Application has started")
  }

  override def onStop(app: play.api.Application) {
    Logger.info("Application shutdown...")
  }

}