package transformer

import akka.http.scaladsl.server.Route
import transformer.routers.TransformerRouter
import transformer.services.{DNFTransformerServiceF, EvaluatorServiceF}

trait TransformerLoader {
  private lazy val transformerRouter = new TransformerRouter(new DNFTransformerServiceF, new EvaluatorServiceF)
  def routes(): Route =
    transformerRouter.routes
}
