name := "Perceptron"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "io.reactivex" %% "rxscala" % "0.26.3",
  "org.scala-lang.modules" %% "scala-async" % "0.9.6",
  "com.typesafe.akka" %% "akka-actor" % "2.4.12"
)
