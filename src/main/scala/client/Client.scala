package client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json._
import transformer.services.DNFTransformerService.{DNFTransformerRequest, TransformerJsonSupport}
import transformer.services.EvaluatorService.{EvaluatorJsonSupport, EvaluatorRequest}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

object Client extends App with SprayJsonSupport with TransformerJsonSupport with EvaluatorJsonSupport {
  implicit val system: ActorSystem                        = ActorSystem("algebraic-transformation-client")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  println("Enter the action you want to perform (transformToDNF/evaluate):")
  println("To exit, type 'exit' and press Enter twice")

  while (true) {
    val action = StdIn.readLine().trim.toLowerCase

    if (action == "exit") {
      println("Exiting...")
      system.terminate()
      System.exit(0)
    } else if (action == "transformtodnf") {
      println("Enter a boolean expression in JSON format (end with a blank line):")
      val expressionJson = readMultilineInput()
      processDNFRequest(expressionJson)
    } else if (action == "evaluate") {
      println("Enter a boolean expression in JSON format (end with a blank line):")
      val expressionJson = readMultilineInput()
      if (expressionJson.contains("Variable")) {
        println("Enter the variable values in JSON format (e.g., {\"x\": true, \"y\": false}):")
        val variablesJson = StdIn.readLine()
        processEvaluateRequest(expressionJson, Some(variablesJson))
      } else {
        processEvaluateRequest(expressionJson, None)
      }

    } else {
      println("Invalid action. Please enter 'transformToDNF', 'evaluate', or 'exit'.")
    }
    println("Enter the action you want to perform (transformToDNF/evaluate) or type 'exit' to quit:")
  }

  private def processDNFRequest(expressionJson: String): Unit =
    try {
      val requestJson   = DNFTransformerRequest(expressionJson)
      val requestEntity = Marshal(requestJson).to[MessageEntity]

      requestEntity.onComplete {
        case Success(entity) => sendRequest(entity, "http://localhost:8080/transformToDNF")
        case Failure(err)    => println(s"Failed to marshal request entity: $err")
      }
    } catch {
      case ex: Exception => println(s"Invalid input: ${ex.getMessage}")
    }

  private def processEvaluateRequest(expressionJson: String, variablesJson: Option[String]): Unit =
    try {
      val variablesMap = variablesJson.map(
        variables => variables.parseJson.convertTo[Map[String, Boolean]]
      )
      val requestJson   = EvaluatorRequest(expressionJson, variablesMap.getOrElse(Map.empty))

      Marshal(requestJson).to[MessageEntity].onComplete {
        case Success(entity) => sendRequest(entity, "http://localhost:8080/evaluate")
        case Failure(err)    => println(s"Failed to marshal request entity: $err")
      }
    } catch {
      case ex: Exception => println(s"Invalid input: ${ex.getMessage}")
    }

  private def sendRequest(requestEntity: MessageEntity, url: String): Unit = {
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = url, entity = requestEntity))

    responseFuture.onComplete {
      case Success(res) =>
        Unmarshal(res.entity).to[String].onComplete {
          case Success(entity) => println(s"Response: $entity")
          case Failure(err)    => println(s"Failed to unmarshal response entity: $err")
        }
      case Failure(err) => println(s"Request failed: $err")
    }
  }

  private def readMultilineInput(): String = {
    val builder      = new StringBuilder
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
