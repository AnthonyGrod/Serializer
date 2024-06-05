package transformer

import akka.http.scaladsl.server.Route
import transformer.routers.TransformerRouter
import transformer.services.DNFTransformerServiceF

trait TransformerLoader {
  private lazy val transformerRouter = new TransformerRouter(new DNFTransformerServiceF)
  def routes(): Route =
    transformerRouter.routes
}
