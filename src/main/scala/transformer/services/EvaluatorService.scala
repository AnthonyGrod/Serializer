package transformer.services

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain._
import spray.json._
import transformer.services.EvaluatorService.{EvaluatorRequest, EvaluatorResponse}
import utils.BooleanExpressionJsonProtocol.BooleanExpressionFormat

import scala.concurrent.{ExecutionContext, Future}

trait EvaluatorService {
  def evaluate(request: EvaluatorRequest): Future[EvaluatorResponse]
}

class EvaluatorServiceF extends EvaluatorService {
  implicit val ec: ExecutionContext = ExecutionContext.global

  override def evaluate(request: EvaluatorRequest): Future[EvaluatorResponse] =
    Future {
      try {
        val expr   = request.json.parseJson.convertTo[BooleanExpression]
        val result = evaluateExpression(expr, request.variable)
        EvaluatorResponse.Up(result.toString)
      } catch {
        case e: IllegalArgumentException => EvaluatorResponse.VariableMissing(e.getMessage)
        case _: Exception                => EvaluatorResponse.Down
      }
    }

  private def evaluateExpression(expr: BooleanExpression, variables: Map[String, Boolean]): Boolean =
    expr match {
      case Variable(name) => variables.getOrElse(name, throw new IllegalArgumentException(s"Variable $name not found"))
      case Not(e)         => !evaluateExpression(e, variables)
      case And(e1, e2)    => evaluateExpression(e1, variables) && evaluateExpression(e2, variables)
      case Or(e1, e2)     => evaluateExpression(e1, variables) || evaluateExpression(e2, variables)
      case True           => true
      case False          => false
    }
}

object EvaluatorService {

  trait EvaluatorJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val EvaluatorRequestFormat: RootJsonFormat[EvaluatorRequest] = jsonFormat2(EvaluatorRequest)
  }

  final case class EvaluatorRequest(json: String, variable: Map[String, Boolean])

  sealed trait EvaluatorResponse
  object EvaluatorResponse {
    case class Up(response: String)                extends EvaluatorResponse
    case class VariableMissing(exWithName: String) extends EvaluatorResponse
    case object Down                               extends EvaluatorResponse
  }
}
