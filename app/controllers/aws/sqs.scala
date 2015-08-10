package controllers.aws
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.{CreateQueueRequest, QueueAttributeName, SendMessageRequest}
import com.typesafe.config.ConfigFactory

/**
 * Created by pdesai on 8/9/15.
 */
object SQS {
  val queueName = "ebook-requests"

  private var client: AmazonSQSClient = null
  private var queueUrl: String  = null

  def init(): Unit = {
    var conf = ConfigFactory.load
    client = {
      val creds = new BasicAWSCredentials(conf.getString("aws.accesskey"), conf.getString("aws.secretKey"))
      val sqs = new AmazonSQSClient(creds)
      sqs.setRegion(Region.getRegion(Regions.US_WEST_2))
      sqs
    }

    queueUrl = {
      val createRequest = new CreateQueueRequest(s"$queueName")
      // This determines how long a message is hidden from other requesters after it's been received (seconds)
      // It should be similar to the expected time taken to process a message

      var attributes = new java.util.HashMap[String, String]
      attributes.put(QueueAttributeName.VisibilityTimeout.name(), "120")

      createRequest.setAttributes(attributes)
      println(s"Starting Amazon SQS queue with name $queueName")
      client.createQueue(createRequest).getQueueUrl
    }
  }

  def add(identifier: String) {
    client.sendMessage(new SendMessageRequest(queueUrl, identifier))
  }
}
