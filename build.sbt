name := "Perceptron"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "io.reactivex" %% "rxscala" % "0.24.1",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.1"
)
