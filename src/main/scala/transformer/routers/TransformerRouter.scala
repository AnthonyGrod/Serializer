package transformer.routers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import pl.iterators.kebs.unmarshallers.KebsUnmarshallers
import transformer.services.DNFTransformerService
import transformer.services.DNFTransformerService.{DNFTransformerRequest, DNFTransformerResponse, JsonSupport}

import scala.concurrent.ExecutionContext

class TransformerRouter(transformerService: DNFTransformerService) extends Directives with KebsUnmarshallers with JsonSupport {
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
    }
  }
}
