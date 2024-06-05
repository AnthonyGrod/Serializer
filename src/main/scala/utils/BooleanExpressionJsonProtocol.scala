package utils

import domain._
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat, deserializationError}

object BooleanExpressionJsonProtocol extends DefaultJsonProtocol {
  implicit object BooleanExpressionFormat extends RootJsonFormat[BooleanExpression] {
    def write(expr: BooleanExpression): JsValue = expr match {
      case True             => JsObject("type" -> JsString("True"))
      case False            => JsObject("type" -> JsString("False"))
      case Variable(symbol) => JsObject("type" -> JsString("Variable"), "symbol" -> JsString(symbol))
      case Not(e)           => JsObject("type" -> JsString("Not"), "expression" -> write(e))
      case Or(e1, e2)       => JsObject("type" -> JsString("Or"), "e1" -> write(e1), "e2" -> write(e2))
      case And(e1, e2)      => JsObject("type" -> JsString("And"), "e1" -> write(e1), "e2" -> write(e2))
    }

    def read(value: JsValue): BooleanExpression =
      value.asJsObject.fields("type") match {
        case JsString("True")     => True
        case JsString("False")    => False
        case JsString("Variable") => Variable(value.asJsObject.fields("symbol").convertTo[String])
        case JsString("Not")      => Not(read(value.asJsObject.fields("expression")))
        case JsString("Or") =>
          val fields = value.asJsObject.fields
          Or(read(fields("e1")), read(fields("e2")))
        case JsString("And") =>
          val fields = value.asJsObject.fields
          And(read(fields("e1")), read(fields("e2")))
        case _ => deserializationError("Unknown BooleanExpression type")
      }
  }
}
