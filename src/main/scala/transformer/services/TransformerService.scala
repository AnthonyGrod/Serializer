package transformer.services

import transformer.services.TransformerService.TransformerServiceResponse

import scala.concurrent.{ExecutionContext, Future}

trait TransformerService {
  def transformDefault(): Future[TransformerServiceResponse]
  def transformCNF(): Future[TransformerServiceResponse]
}

class TransformerServiceF extends TransformerService {
  implicit val ec: ExecutionContext = ExecutionContext.global
  def transformDefault(): Future[TransformerServiceResponse] = Future {
    TransformerServiceResponse.Up("transformedDefault")
  }
  def transformCNF(): Future[TransformerServiceResponse] = Future {
    TransformerServiceResponse.Up("transformedCNF")
  }
}

object TransformerService {
  sealed trait TransformerServiceResponse
  object TransformerServiceResponse {
    case class Up(response: String) extends TransformerServiceResponse
    case object Down         extends TransformerServiceResponse
  }
}
