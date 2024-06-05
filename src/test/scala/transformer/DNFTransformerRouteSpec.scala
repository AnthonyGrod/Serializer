package transformer

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import domain.BooleanExpressionJson
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._
import transformer.routers.TransformerRouter
import transformer.services.DNFTransformerService.{DNFTransformerRequest, JsonSupport}
import transformer.services.DNFTransformerServiceF

class DNFTransformerRouteSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with ScalaFutures with JsonSupport {

  val mockService = new DNFTransformerServiceF
  val router = new TransformerRouter(mockService)

  "TransformerRouter" should {

    "return transformed response for valid simple DNFTransformerRequest" in {
      val requestJson = DNFTransformerRequest(BooleanExpressionJson.jsonSimple)
      val requestEntity = Marshal(requestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> router.routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String].parseJson shouldEqual BooleanExpressionJson.jsonSimpleResponseDNF.parseJson
      }
    }

    "return transformed response for valid complex DNFTransformerRequest" in {
      val requestJson = DNFTransformerRequest(BooleanExpressionJson.jsonComplex)
      val requestEntity = Marshal(requestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> router.routes ~> check {
        status should ===(StatusCodes.OK)
        responseAs[String].parseJson shouldEqual BooleanExpressionJson.jsonComplexResponseDNF.parseJson
      }
    }

    "return BadRequest for malformed DNFTransformerRequest (missing closing bracket)" in {
      val invalidRequestJson = DNFTransformerRequest(BooleanExpressionJson.jsonMalformed)
      val requestEntity = Marshal(invalidRequestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> Route.seal(router.routes) ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "return BadRequest for malformed DNFTransformerRequest (missing field)" in {
      val invalidRequestJson = DNFTransformerRequest(BooleanExpressionJson.jsonMissingField)
      val requestEntity = Marshal(invalidRequestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> Route.seal(router.routes) ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "return BadRequest for malformed DNFTransformerRequest (incorrect field name)" in {
      val invalidRequestJson = DNFTransformerRequest(BooleanExpressionJson.jsonIncorrectFieldName)
      val requestEntity = Marshal(invalidRequestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> Route.seal(router.routes) ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "return BadRequest for malformed DNFTransformerRequest (unknown type)" in {
      val invalidRequestJson = DNFTransformerRequest(BooleanExpressionJson.jsonUnknownType)
      val requestEntity = Marshal(invalidRequestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> Route.seal(router.routes) ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "return BadRequest for malformed DNFTransformerRequest (incomplete expression)" in {
      val invalidRequestJson = DNFTransformerRequest(BooleanExpressionJson.jsonIncompleteNot)
      val requestEntity = Marshal(invalidRequestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> Route.seal(router.routes) ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }

    "return BadRequest for empty DNFTransformerRequest" in {
      val invalidRequestJson = DNFTransformerRequest(BooleanExpressionJson.emptyJson)
      val requestEntity = Marshal(invalidRequestJson).to[MessageEntity].futureValue

      val request = Post("/transformToDNF").withEntity(requestEntity)

      request ~> Route.seal(router.routes) ~> check {
        status should ===(StatusCodes.BadRequest)
      }
    }
  }
}
