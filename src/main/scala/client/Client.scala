package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import transformer.services.DNFTransformerService.{DNFTransformerRequest, JsonSupport}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

object Client extends SprayJsonSupport with JsonSupport {
  implicit val system: ActorSystem = ActorSystem("algebraic-transformation-client")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  def main(args: Array[String]): Unit = {

    println("Enter a boolean expression in JSON format (end with a blank line):")

    while (true) {
      val expressionJson = readMultilineInput()

      if (expressionJson.trim.equalsIgnoreCase("exit")) {
        println("Exiting...")
        system.terminate()
        return
      }

      processRequest(expressionJson)
      println("Enter another boolean expression in JSON format or type 'exit' to quit:")
    }
  }

  private def processRequest(expressionJson: String): Unit = {
    try {
      val requestJson = DNFTransformerRequest(expressionJson)
      val requestEntity = Marshal(requestJson).to[MessageEntity]

      requestEntity.onComplete {
        case Success(entity) => sendRequest(entity)
        case Failure(err) => println(s"Failed to marshal request entity: $err")
      }
    } catch {
      case ex: Exception => println(s"Invalid input: ${ex.getMessage}")
    }
  }

  private def sendRequest(requestEntity: MessageEntity): Unit = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8080/transformToDNF",
      entity = requestEntity
    ))

    responseFuture.onComplete {
      case Success(res) =>
        Unmarshal(res.entity).to[String].onComplete {
          case Success(entity) => println(s"Response: $entity")
          case Failure(err) => println(s"Failed to unmarshal response entity: $err")
        }
      case Failure(err) => println(s"Request failed: $err")
    }
  }

  private def readMultilineInput(): String = {
    val builder = new StringBuilder
    var line: String = null

    do {
      line = StdIn.readLine()
      if (line != null && line.trim.nonEmpty) {
        builder.append(line)
        builder.append("\n")
      }
    } while (line != null && line.trim.nonEmpty)

    builder.toString()
  }
}
