package com.netcloud.perceptron

import scala.concurrent.Future
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global

case class Edge(value: Future[Double], weight: Double)

/**
 * Created by keinmark on 05.10.14.
 */
class Perceptron {

  val inputEdges: List[Edge] = List[Edge](Edge(Future {1}, -1.5), Edge(Future {0}, 1), Edge(Future {0}, 1))

  def activate() {
    val inputs = Future sequence inputEdges.map(_.value).toList
    inputs onComplete {
      case Success(results) => {
        val res = Future {Perceptron.sigmoid(inputEdges.map(e => e.weight * e.value.value.get.get).sum)}
        res onSuccess {
          case y => println(y)
        }
      }
    }
  }
}

object Perceptron {
  def sigmoid(value: Double): Double = {
    return (1 / (1 + Math.exp(-1 * value)))
  }

  def main(args: Array[String]) {
    val p = new Perceptron()
    p.activate()
  }
}
