package transformer.routers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import pl.iterators.kebs.unmarshallers.KebsUnmarshallers
import transformer.services.{DNFTransformerService, EvaluatorService}
import transformer.services.DNFTransformerService.{DNFTransformerRequest, DNFTransformerResponse, TransformerJsonSupport}
import transformer.services.EvaluatorService.{EvaluatorJsonSupport, EvaluatorRequest, EvaluatorResponse}

import scala.concurrent.ExecutionContext

class TransformerRouter(transformerService: DNFTransformerService, evaluatorService: EvaluatorService)
    extends Directives
    with KebsUnmarshallers
    with TransformerJsonSupport
    with EvaluatorJsonSupport {
  def routes: Route = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    pathPrefix("transformToDNF") {
      (pathEndOrSingleSlash & post & entity(as[DNFTransformerRequest])) { transformRequest =>
        complete {
          transformerService.transformToDNF(transformRequest).map {
            case DNFTransformerResponse.Up(response) =>
              StatusCodes.OK -> response
            case DNFTransformerResponse.Down =>
              StatusCodes.BadRequest -> "Invalid JSON expression for boolean expression format"
          }
        }
      }
    } ~ pathPrefix("evaluate") {
      (pathEndOrSingleSlash & post & entity(as[EvaluatorRequest])) { evaluateRequest =>
        complete {
          evaluatorService.evaluate(evaluateRequest).map {
            case EvaluatorResponse.Up(response) =>
              StatusCodes.OK -> response
            case EvaluatorResponse.VariableMissing(exWithName) =>
              StatusCodes.BadRequest -> exWithName
            case EvaluatorResponse.Down =>
              StatusCodes.BadRequest -> "Invalid JSON expression for boolean expression format"
          }
        }
      }
    }
  }
}
