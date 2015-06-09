package com.netcloud.perceptron

import java.util.concurrent.TimeUnit
import scala.concurrent.{ Await, Promise, Future }
import scala.languageFeature.postfixOps
import scala.async.Async.{ async, await }
import scala.concurrent.ExecutionContext.Implicits.global
import rx.lang.scala.Observable

/**
 * A @{Perceptron} is the core component of a neural net.
 * It consists of multiple inputchannel and multiple outputchannel.
 * As soon as all inputs are defined it calculates the output value
 * and broadcasts it to all outputchannel.
 */
class Perceptron private (val ins: List[InputEdge], val outs: List[OutputEdge]) {

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
    val zero = Observable[(Double, Double)] { x => }
    val bigStream = inputEdges.map(x => x.channel).foldLeft(zero)((el, acc) => acc.merge(el))
    bigStream
      .take(ins.size)
      .scan(0.0)((acc, el) => acc + (el._1 * el._2))
      .subscribe(v => activate(v))
  }

  /**
   * The activation function
   */
  private[this] def activate(value: Double): Unit = {
    async {
      val act = Perceptron.sigmoid(value)
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
  def apply(ins: List[InputEdge], outs: List[OutputEdge]): Perceptron = {
    val perceptron = new Perceptron(ins, outs)
    perceptron.init()
    perceptron
  }

  def sigmoid(value: Double): Double = {
    1 / (1 + Math.exp(-1 * value))
  }
}
