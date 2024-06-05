scalaVersion := "2.13.12"

name := "serializer"
organization := "ch.epfl.scala"

version := "1.0"

val scalaParserCombinatorsVersion = "2.3.0"
val akkaHttpVersion               = "10.2.10"
val akkaVersion                   = "2.6.20"
val catsCoreVersion               = "2.9.0"
val typesafeSslConfigCoreV        = "0.6.1"
val kebsVersion                   = "1.8.1"
val circeVersion                  = "0.14.1"

lazy val root = (project in file(".")).
 settings(
   inThisBuild(List(
     organization := "ch.epfl.scala",
     scalaVersion := "2.13.12"
    )),
   name := "serializer",
   libraryDependencies ++= Seq(
     "org.scala-lang.modules"        %% "scala-parser-combinators"   % scalaParserCombinatorsVersion,
     "com.typesafe"                  %% "ssl-config-core"            % typesafeSslConfigCoreV,
     "com.typesafe.akka"             %% "akka-actor"                 % akkaVersion,
     "com.typesafe.akka"             %% "akka-stream"                % akkaVersion,
     "com.typesafe.akka"             %% "akka-http"                  % akkaHttpVersion,
     "com.typesafe.akka"             %% "akka-http-core"             % akkaHttpVersion,
     "com.typesafe.akka"             %% "akka-http-spray-json"       % akkaHttpVersion,
     "org.typelevel"                 %% "cats-core"                  % catsCoreVersion,
     "pl.iterators"                  %% "kebs-akka-http"             % kebsVersion,
     "io.circe"                      %% "circe-core"                 % circeVersion,
     "io.circe"                      %% "circe-generic"              % circeVersion,
     "io.circe"                      %% "circe-parser"               % circeVersion,
   )
 )

