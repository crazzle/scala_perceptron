package com.netcloud.perceptron

import com.netcloud.perceptron.Perceptron.Activatable
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
case class Perceptron(name: String,
                      inputEdges: List[InputEdge],
                      outputEdge: OutputEdge,
                      f: (Double) => Double = Perceptron.sigmoid) extends Activatable {

  /**
    * Run at instantiation to initialize the perceptrons input-stream
    */
  init()

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
        async {
          if (activations.size == inputEdges.size) {
            activate(activations)
          }
        }
      }
  }

  /**
   * The activation function
   */
  override def activate(values: Seq[(Double, Double)]): Double = {
    val value = values.foldLeft(0.0)((acc, el) => acc + (el._1 * el._2))
    val activation = f(value)
    outputEdge.push(activation)
    activation
  }

  /**
    * provide the channels
    */
  override def channels = inputEdges
}
object Perceptron {
  /**
    * Sigmoid function that is used during the activation
    */
  def sigmoid(value: Double): Double = {
    1 / (1 + Math.exp(-1 * value))
  }

  /**
    * A perceptron is an Activatable. That means it can be activated if it has
    * enough values pushed over the edges
    */
  trait Activatable {
    def activate(values: Seq[(Double, Double)]): Double
    def channels : List[InputEdge]
  }
}
