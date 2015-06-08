package com.netcloud.perceptron

import java.util.concurrent.TimeUnit
import scala.concurrent.{ Await, Promise, Future }
import scala.languageFeature.postfixOps
import scala.async.Async.{ async, await }
import scala.concurrent.ExecutionContext.Implicits.global

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
   * Activations with weight stored per input edge
   */
  private[this]type ActivationWithWeight = (Double, Double)
  private[this] var activations = Vector[ActivationWithWeight]() // TODO: Get rid of it!!!!

  /**
   * Initializes the perceptron by applying the right
   * function to all input edges
   */
  def init(): Unit = {
    /**
     * TODO: Implement a merge in @{WiringEdge} to get rid of the 
     * activations and thus of the synchronized block in listen.
     */
    inputEdges.foreach { in => in.listen(listen) }
  }

  /**
   * Listens for new activations and if all ready activates
   * the perceptron
   */
  private[this] def listen: ((Double, Double)) => Unit = { activationWithWeight =>
    async {
      synchronized {
        activations = activations :+ activationWithWeight
        if (activations.size == inputEdges.size) {
          activate()
          activations = Vector[ActivationWithWeight]()
        }
      }
    }
  }

  /**
   * The activation function
   */
  private[this] def activate(): Unit = {
    val act = Perceptron.sigmoid(activations.map(_ match {
      case (activation, weight) => activation * weight
      case _                    => throw new IllegalArgumentException("That was not an ActivationWithWeight")
    }).sum)
    broadcast(act)
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
