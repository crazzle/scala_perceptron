package com.netcloud.example

import com.netcloud.perceptron.{Perceptron, WiringEdge}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object AutomatedExample extends App {

  val numInputs = 2
  val layer1Size = 2
  val outSize = 1

  val inputEdges = Seq.fill(numInputs)(WiringEdge(1))

  val layer1 = Seq.fill(layer1Size)(Perceptron(inputEdges, WiringEdge(1)))

  val layer1OutEdges = layer1.map{p => p.outputEdge}

  val out = Perceptron(layer1OutEdges, WiringEdge(1))

  val p = Promise[Double]()
  out.outputEdge.listen {
    case (activation, weight) => {
      p.success(activation)
    }
  }

  inputEdges.foreach(_.push(1))

  val result = Await.result(p.future,Duration.Inf)

  println("the result is : " + result)

  System.exit(0)



}
