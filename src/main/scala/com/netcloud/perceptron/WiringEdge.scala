package com.netcloud.perceptron

import rx.lang.scala.Subject

import scala.util.Random

/**
 * Abstraction for an edge, that contains a weight
 */
trait Edge{
  val channel : Subject[(Double, Double)]
  val weight : Double
}

/**
 * Abstraction for edges acting as an input to a perceptron
 */
trait InputEdge extends Edge{
  def listen(f : ((Double,Double)) => Unit) : Unit
}

/**
 * Abstraction for edges acting as an output from a perceptron
 */
trait OutputEdge extends Edge{
  def push(activation : Double) : Unit
}

/**
 * An edge wires @{Perceptron}s up
 * It contains a channel and a weight
 * <<- It is a case class to use the copy function ->>
 */
case class WiringEdge private (weight : Double, channel : Subject[(Double, Double)]) extends InputEdge with OutputEdge{

  /**
   * Pushing the activation value from one perceptron to another
   */
  def push(activation : Double) : Unit = {
    channel.onNext((activation, weight))
  }

  /**
   * Listen to the channel to receive new activation values from a perceptron
   */
  def listen(f : ((Double, Double)) => Unit) : Unit = {
    channel.subscribe(f)
  }

}
object WiringEdge{
	def apply(weight : Double = math.random): WiringEdge = new WiringEdge(weight, Subject[(Double, Double)]())
  def apply(): WiringEdge = new WiringEdge(0, Subject[(Double, Double)]())
}
