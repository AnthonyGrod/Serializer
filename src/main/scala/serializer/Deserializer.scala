package serializer

import domain.BooleanExpression

trait Deserializer {
  def deserialize(json: String): BooleanExpression
}

object Deserializer {
  def apply(): Deserializer = new DeserializerF

  private final class DeserializerF extends Deserializer {
    override def deserialize(json: String): BooleanExpression = {
      import spray.json._
      import utils.BooleanExpressionJsonProtocol.BooleanExpressionFormat

      json.parseJson.convertTo[BooleanExpression]
    }
  }
}
