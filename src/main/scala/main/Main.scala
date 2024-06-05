package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import transformer.TransformerLoader

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

object Main extends App with TransformerLoader {
  implicit val system: ActorSystem                        = ActorSystem("my-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  Http().newServerAt("localhost", 8080).bindFlow(routes())
  println(s"Server online at http://localhost:8080/transform/default \n")

  Await.result(system.whenTerminated, Duration.Inf)
}
