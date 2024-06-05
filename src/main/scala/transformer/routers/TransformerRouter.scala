package transformer.routers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import pl.iterators.kebs.unmarshallers.KebsUnmarshallers
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol.jsonFormat1
import transformer.services.DNFTransformerService
import transformer.services.DNFTransformerService.{DNFTransformerRequest, DNFTransformerResponse, JsonSupport}

import scala.concurrent.ExecutionContext

class TransformerRouter(transformerService: DNFTransformerService)
  extends Directives
  with KebsUnmarshallers
  with JsonSupport {
  def routes: Route = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    pathPrefix("transformToDNF") {
      (pathEndOrSingleSlash & post & entity(as[DNFTransformerRequest])) { transformRequest =>
        complete {
          transformerService.transformToDNF(transformRequest).map {
            case DNFTransformerResponse.Up(response) =>
              StatusCodes.OK -> response
            case DNFTransformerResponse.Down =>
              StatusCodes.InternalServerError -> "Error executing query"
          }
        }
      }
    }
  }
}
