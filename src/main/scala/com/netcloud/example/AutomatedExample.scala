package com.netcloud.example

import com.netcloud.perceptron.Perceptron.WiringResult
import com.netcloud.perceptron.{Perceptron, WiringEdge}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object AutomatedExample extends App {

  def standardEdge() = WiringEdge(1)


  val numInputs = 2
  val layer1Size = 2
  val outSize = 1

  val inputEdges = Range(0,numInputs).map{
    _ => {
      standardEdge()
    }
  }.toList

  val layer1 = Range(0,layer1Size).map{
    _ => {
      Perceptron(inputEdges, WiringEdge(1), WiringEdge(1))
    }
  }.toList

  val layer1OutEdges = layer1.map{case WiringResult(p, edgeOut) => edgeOut}

  val out = Range(0, outSize).map{
    _ => {
      Perceptron(layer1OutEdges, WiringEdge(1), WiringEdge(1))
    }
  }

  val p = Promise[Double]()
  out.head.outputEdge.listen {
    case (activation, weight) => {
      p.success(activation)
    }
  }

  inputEdges.foreach(_.push(1))


  val result = Await.result(p.future,Duration.Inf)

  println("the result is : " + result)

  System.exit(0)



}
