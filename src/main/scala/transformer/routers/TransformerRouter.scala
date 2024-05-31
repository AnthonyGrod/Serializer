package transformer.routers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import transformer.services.TransformerService

import scala.concurrent.ExecutionContext

class TransformerRouter(transformerService: TransformerService) extends Directives {
  def routes: Route = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    pathPrefix("transform") {
      path("default") {
        (pathEndOrSingleSlash & get) {
          complete {
            transformerService.transformDefault().map {
              case TransformerService.TransformerServiceResponse.Up(response) =>
                StatusCodes.OK -> ("You asked for " ++ response)
              case TransformerService.TransformerServiceResponse.Down =>
                StatusCodes.InternalServerError -> "Error executing query"
            }
          }
        }
      } ~
      path("cnf") {
        (pathEndOrSingleSlash & get) {
          complete {
            transformerService.transformCNF().map {
              case TransformerService.TransformerServiceResponse.Up(response) =>
                StatusCodes.OK -> ("You asked for " ++ response)
              case TransformerService.TransformerServiceResponse.Down =>
                StatusCodes.InternalServerError -> "Error executing query"
            }
          }
        }
      }
    }
  }
}
