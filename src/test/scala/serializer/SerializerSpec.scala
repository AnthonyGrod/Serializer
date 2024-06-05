package serializer

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import domain.BooleanExpression
import domain.BooleanExpressionJson._
import spray.json._
import utils.BooleanExpressionJsonProtocol._

class SerializerSpec extends AnyWordSpec with Matchers {
  val serializer: Serializer = Serializer()
  val deserializer: Deserializer = Deserializer()

  "Serializer" should {
    "serialize a simple BooleanExpression to JSON" in {

      val expression: BooleanExpression = jsonSimple.parseJson.convertTo[BooleanExpression]
      val serialized = serializer.serialize(expression)

      serialized.parseJson shouldEqual jsonSimple.parseJson
    }

    "serialize a complex BooleanExpression to JSON" in {

      val expression: BooleanExpression = jsonComplex.parseJson.convertTo[BooleanExpression]
      val serialized = serializer.serialize(expression)

      serialized.parseJson shouldEqual jsonComplex.parseJson
    }
  }

  "Deserializer" should {
    "deserialize a simple JSON string to BooleanExpression" in {

      val expression: BooleanExpression = deserializer.deserialize(jsonSimple).getOrElse(fail("Deserialization failed"))
      val expected = jsonSimple.parseJson.convertTo[BooleanExpression]

      expression shouldEqual expected
    }

    "deserialize a complex JSON string to BooleanExpression" in {

      val expression: BooleanExpression = deserializer.deserialize(jsonComplex).getOrElse(fail("Deserialization failed"))
      val expected = jsonComplex.parseJson.convertTo[BooleanExpression]

      expression shouldEqual expected
    }

    "return an error for malformed JSON" in {

      val result = deserializer.deserialize(jsonMalformed)
      result should be (Symbol("left"))
      result.swap.getOrElse("") should include ("Invalid JSON")
    }

    "return an error for JSON with missing field" in {

      val result = deserializer.deserialize(jsonMissingField)
      result should be (Symbol("left"))
      result.swap.getOrElse("") should include ("Invalid JSON")
    }

    "return an error for JSON with incorrect field name" in {

      val result = deserializer.deserialize(jsonIncorrectFieldName)
      result should be (Symbol("left"))
      result.swap.getOrElse("") should include ("Invalid JSON")
    }

    "return an error for JSON with unknown type" in {

      val result = deserializer.deserialize(jsonUnknownType)
      result should be (Symbol("left"))
      result.swap.getOrElse("") should include ("Invalid JSON")
    }

    "return an error for JSON with incomplete expression" in {

      val result = deserializer.deserialize(jsonIncompleteNot)
      result should be (Symbol("left"))
      result.swap.getOrElse("") should include ("Invalid JSON")
    }

    "return an error for empty JSON" in {

      val result = deserializer.deserialize(emptyJson)
      result should be (Symbol("left"))
      result.swap.getOrElse("") should include ("Invalid JSON")
    }
  }
}
