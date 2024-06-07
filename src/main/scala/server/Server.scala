package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import transformer.TransformerLoader

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.StdIn

object Server extends App with TransformerLoader {
  implicit val system: ActorSystem                        = ActorSystem("my-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val server = Http().newServerAt("localhost", 8080).bindFlow(routes())

  println(s"Server online at http://localhost:8080/transformToDNF")
  println("Press Enter to stop the server\n")
  StdIn.readLine()
  server
    .flatMap(_.unbind())
    .onComplete { _ =>
      println("Server stopped.")
      system.terminate()
    }
  Await.result(system.whenTerminated, Duration.Inf)
}
