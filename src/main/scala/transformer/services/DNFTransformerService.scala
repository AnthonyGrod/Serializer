package transformer.services

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain._
import spray.json._
import transformer.services.DNFTransformerService.{DNFTransformerRequest, DNFTransformerResponse}
import utils.BooleanExpressionJsonProtocol.BooleanExpressionFormat

import scala.concurrent.{ExecutionContext, Future}

trait DNFTransformerService {
  def transformToDNF(request: DNFTransformerRequest): Future[DNFTransformerResponse]
}

class DNFTransformerServiceF extends DNFTransformerService {
  implicit val ec: ExecutionContext = ExecutionContext.global

  override def transformToDNF(request: DNFTransformerRequest): Future[DNFTransformerResponse] =
    Future {
      try {
        val expr    = request.json.parseJson.convertTo[BooleanExpression]
        val dnfExpr = convertToDNF(expr)
        DNFTransformerResponse.Up(dnfExpr.toJson.toString())
      } catch {
        case _: Exception => DNFTransformerResponse.Down
      }
    }

  private def convertToDNF(expr: BooleanExpression): BooleanExpression =
    expr match {
      case Not(Not(e))      => convertToDNF(e)
      case Not(And(e1, e2)) => Or(convertToDNF(Not(e1)), convertToDNF(Not(e2)))
      case Not(Or(e1, e2))  => And(convertToDNF(Not(e1)), convertToDNF(Not(e2)))
      case And(e1, e2) =>
        (e1, e2) match {
          case (Or(e1, e2), e3) => convertToDNF(Or(And(e1, e3), And(e2, e3)))
          case (e3, Or(e1, e2)) => convertToDNF(Or(And(e1, e3), And(e2, e3)))
          case (e1, e2)         => And(convertToDNF(e1), convertToDNF(e2))
        }
      case Or(e1, e2) => Or(convertToDNF(e1), convertToDNF(e2))
      case _          => expr
    }
}

object DNFTransformerService {
  sealed trait DNFTransformerResponse

  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val dnfTransformerRequestFormat: RootJsonFormat[DNFTransformerRequest] = jsonFormat1(DNFTransformerRequest)
  }

  final case class DNFTransformerRequest(json: String)

  object DNFTransformerResponse {
    case class Up(response: String) extends DNFTransformerResponse

    case object Down extends DNFTransformerResponse
  }
}
