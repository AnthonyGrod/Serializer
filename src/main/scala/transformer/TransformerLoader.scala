package transformer

import akka.http.scaladsl.server.Route
import transformer.routers.TransformerRouter
import transformer.services.TransformerServiceF

trait TransformerLoader {
  private lazy val transformerRouter = new TransformerRouter(new TransformerServiceF)
  def routes(): Route = {
    transformerRouter.routes
  }
}
