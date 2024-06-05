package serializer

import domain.BooleanExpression

trait Serializer {
  def serialize(expression: BooleanExpression): String
}

object Serializer {
  def apply(): Serializer = new SerializerF

  private final class SerializerF extends Serializer {
    override def serialize(expression: BooleanExpression): String = {
      import spray.json._
      import utils.BooleanExpressionJsonProtocol.BooleanExpressionFormat

      expression.toJson.toString()
    }
  }
}
