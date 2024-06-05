package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import transformer.TransformerLoader

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Server extends App with TransformerLoader {
  implicit val system: ActorSystem                        = ActorSystem("my-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val server = Http().newServerAt("localhost", 8080).bindFlow(routes())

  println(s"Server online at http://localhost:8080/transformToDNF \n")
  StdIn.readLine() // run until user presses return
  server
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
