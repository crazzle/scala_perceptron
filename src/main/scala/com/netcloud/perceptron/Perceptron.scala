package com.netcloud.perceptron

import java.util.concurrent.TimeUnit
import scala.concurrent.{ Await, Promise, Future }
import scala.languageFeature.postfixOps
import scala.async.Async.{ async, await }
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
    val ins: List[InputEdge], 
    val outs: List[OutputEdge], 
    val sig: Boolean) {

  /**
   * Edges a perceptron receives activation values from
   */
  private[this] val inputEdges: List[InputEdge] = ins

  /**
   * Edges a perceptron broadcasts its activation value to
   */
  private[this] val outputEdges: List[OutputEdge] = outs

  /**
   * Initializes the perceptron by applying the right
   * function to all input edges
   */
  def init(): Unit = {
    val typedEmpty = Observable[(Double, Double)] { x => }
    inputEdges
      .map(x => x.channel)
      .foldLeft(typedEmpty)((el, acc) => acc.merge(el))
      .scan((0l, Queue.empty[(Double, Double)]))((acc, el) => 
        (acc, el) match {
          case ((count, activations), activation) => (count + 1, activations :+ activation)
      })
      .subscribe { tuple =>
        val (count, activations) = tuple
        if(count > 0 && count % ins.size == 0){
          activate(activations.takeRight(ins.size))
        }
      }
  }

  /**
   * The activation function
   */
  private[this] def activate(values: Seq[(Double, Double)]): Unit = {
    async {
      val value = values.foldLeft(0.0)((acc, el) => acc + (el._1 * el._2))
      val act = if (sig) { Perceptron.sigmoid(value) } else value
      broadcast(act)
    }
  }

  /**
   * Broadcasts an activation to all outputs
   */
  private[this] def broadcast(activation: Double) = {
    outputEdges.foreach { out => out.push(activation) }
  }
}

object Perceptron {
  def apply(name: String, ins: List[InputEdge], outs: List[OutputEdge], sig: Boolean = false): Perceptron = {
    val perceptron = new Perceptron(name, ins, outs, sig)
    perceptron.init()
    perceptron
  }

  def sigmoid(value: Double): Double = {
    1 / (1 + Math.exp(-1 * value))
  }
}
