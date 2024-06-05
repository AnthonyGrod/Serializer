scalaVersion := "2.13.12"

name := "serializer"
organization := "ch.epfl.scala"

version := "1.0"

val akkaHttpVersion               = "10.2.10"
val akkaVersion                   = "2.6.20"
val kebsVersion                   = "1.8.1"
val scalaTestVersion              = "3.2.9"

lazy val root = (project in file(".")).
 settings(
   inThisBuild(List(
     organization := "ch.epfl.scala",
     scalaVersion := "2.13.12"
    )),
   name := "serializer",
   libraryDependencies ++= Seq(
     "com.typesafe.akka"             %% "akka-actor"                 % akkaVersion,
     "com.typesafe.akka"             %% "akka-stream"                % akkaVersion,
     "com.typesafe.akka"             %% "akka-testkit"               % akkaVersion % Test,
     "com.typesafe.akka"             %% "akka-http"                  % akkaHttpVersion,
     "com.typesafe.akka"             %% "akka-http-core"             % akkaHttpVersion,
     "com.typesafe.akka"             %% "akka-http-spray-json"       % akkaHttpVersion,
     "com.typesafe.akka"             %% "akka-http-testkit"          % akkaHttpVersion % Test,
     "org.scalatest"                 %% "scalatest"                  % scalaTestVersion % Test,
     "pl.iterators"                  %% "kebs-akka-http"             % kebsVersion,
   )
 )

