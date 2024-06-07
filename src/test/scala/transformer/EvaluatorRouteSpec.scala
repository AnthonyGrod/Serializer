package transformer

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import transformer.routers.TransformerRouter
import transformer.services.EvaluatorService.{EvaluatorJsonSupport, EvaluatorRequest}
import transformer.services.{DNFTransformerServiceF, EvaluatorServiceF}

class EvaluatorRouteSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with ScalaFutures
  with EvaluatorJsonSupport {

  val transformerService = new DNFTransformerServiceF
  val evaluatorService = new EvaluatorServiceF
  val router = new TransformerRouter(transformerService, evaluatorService)
  val evaluateRoute = "/evaluate"

  "TransformerRouter" should {
    "evaluate a simple Variable expression with x set to true" in {
      val evaluateRequestJson = EvaluatorRequest("{\"type\":\"Variable\",\"symbol\":\"x\"}", Map("x" -> true))
      val requestEntity = Marshal(evaluateRequestJson).to[MessageEntity].futureValue

      val request = Post("/evaluate").withEntity(requestEntity)

      request ~> router.routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "true"
      }
    }

    "evaluate a simple And expression with x and y set to true" in {
      val evaluateRequestJson = EvaluatorRequest(
        "{\"type\":\"And\",\"e1\":{\"type\":\"Variable\",\"symbol\":\"x\"},\"e2\":{\"type\":\"Variable\",\"symbol\":\"y\"}}",
        Map("x" -> true, "y" -> true)
      )
      val requestEntity = Marshal(evaluateRequestJson).to[MessageEntity].futureValue

      val request = Post("/evaluate").withEntity(requestEntity)

      request ~> router.routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String] shouldEqual "true"
      }
    }

    "return an error when variable y is not set in And expression" in {
      val evaluateRequestJson = EvaluatorRequest(
        "{\"type\":\"And\",\"e1\":{\"type\":\"Variable\",\"symbol\":\"x\"},\"e2\":{\"type\":\"Variable\",\"symbol\":\"y\"}}",
        Map("x" -> true)
      )
      val requestEntity = Marshal(evaluateRequestJson).to[MessageEntity].futureValue

      val request = Post("/evaluate").withEntity(requestEntity)

      request ~> router.routes ~> check {
        status should ===(StatusCodes.BadRequest)
        responseAs[String] should include("Variable y not found")
      }
    }
  }
}


