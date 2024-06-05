package serializer

import domain.BooleanExpression
import spray.json._
import utils.BooleanExpressionJsonProtocol._

trait Deserializer {
  def deserialize(json: String): Either[String, BooleanExpression]
}

object Deserializer {
  def apply(): Deserializer = new DeserializerF

  private final class DeserializerF extends Deserializer {
    override def deserialize(json: String): Either[String, BooleanExpression] =
      try
        Right(json.parseJson.convertTo[BooleanExpression])
      catch {
        case _: Exception => Left("Invalid JSON expression for boolean expression format")
      }
  }
}
