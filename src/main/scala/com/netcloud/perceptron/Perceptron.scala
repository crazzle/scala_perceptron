package com.netcloud.perceptron

import scala.languageFeature.postfixOps
import scala.async.Async.async
import scala.concurrent.ExecutionContext.Implicits.global
import rx.lang.scala.Observable
import scala.collection.immutable.Queue

/**
 * A @{Perceptron} is the core component of a neural net.
 * It consists of multiple inputchannel and multiple outputchannel.
 * As soon as all inputs are defined it calculates the output value
 * and broadcasts it to all outputchannel.
 */
class Perceptron private (val name: String,
                          val inputEdges: List[InputEdge],
                          val outputEdge: OutputEdge,
                          val f: (Double) => Double) {

  /**
   * Initializes the perceptron by applying the right
   * function to all input edges
   */
  private def init(): Unit = {
    val typedEmpty = Observable[(Double, Double)] { x => }
    inputEdges
      .map(x => x.channel)
      .foldLeft(typedEmpty)((el, acc) => acc.merge(el))
      .scan(Queue.empty[(Double, Double)])((acc, el) =>
        (acc, el) match {
          case (list, activation) =>
            if (list.size < inputEdges.size) {
              list :+ activation
            } else {
              Queue.empty[(Double, Double)] :+ activation
            }
        })
      .subscribe { activations =>
        if (activations.size == inputEdges.size) {
          activate(activations)
        }
      }
  }

  /**
   * The activation function
   */
  private[this] def activate(values: Seq[(Double, Double)]): Unit = {
    async {
      val value = values.foldLeft(0.0)((acc, el) => acc + (el._1 * el._2))
      val act = f(value)
      broadcast(act)
    }
  }

  /**
   * Broadcasts an activation to all outputs
   */
  private[this] def broadcast(activation: Double) = {
    outputEdge.push(activation)
  }
}

object Perceptron {
  def apply(name: String, 
      ins: List[InputEdge], 
      outs: OutputEdge,
      f: (Double) => Double = sigmoid): Perceptron = {
    val perceptron = new Perceptron(name, ins, outs, f)
    perceptron.init()
    perceptron
  }

  def sigmoid(value: Double): Double = {
    1 / (1 + Math.exp(-1 * value))
  }
}
