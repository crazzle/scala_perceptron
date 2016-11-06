package com.netcloud.example

import java.util.concurrent.{Executors}
import akka.dispatch.ExecutionContexts
import com.netcloud.perceptron.{Perceptron, WiringEdge}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object AutomatedExample extends App {

  val pec = ExecutionContexts.fromExecutor(Executors.newFixedThreadPool(4))
  val wec = ExecutionContexts.fromExecutor(Executors.newFixedThreadPool(6))

  val numInputs = 750
  val layer1Size = 750
  val outSize = 1

  val inputEdges = Seq.fill(numInputs)(WiringEdge(1)(wec))

  val layer1 = Seq.fill(layer1Size)(Perceptron(inputEdges, WiringEdge(1)(wec))(pec))

  val layer1OutEdges = layer1.map{p => p.output}

  val out = Perceptron(layer1OutEdges, WiringEdge(1)(wec))(pec)

  val p = Promise[Double]()
  out.output.listen {
    case (activation, weight) => {
      p.success(activation)
    }
  }

  val now = System.currentTimeMillis().toDouble
  inputEdges.foreach(_.push(1))
  val intermediate = System.currentTimeMillis().toDouble
  val result = Await.result(p.future,Duration.Inf)
  val after = System.currentTimeMillis().toDouble

  val input = ((intermediate - now))/1000
  val calculation = (after - intermediate)/1000
  val whole = input + calculation
  println("the result is : " + result + " after " + whole + " seconds." )
  println("input time: " +  input)
  println("calculation time layer 1: " + calculation)

  System.exit(0)
}
