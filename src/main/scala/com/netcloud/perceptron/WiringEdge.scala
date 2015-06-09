package com.netcloud.perceptron

import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subject


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
  def listen(f : ((Double,Double)) => Unit)
  def merge(that: Observable[(Double, Double)]) : Observable[(Double, Double)]
}

/**
 * Abstraction for edges acting as an output from a perceptron
 */
trait OutputEdge extends Edge{
  def push(activation : Double)
}

/**
 * An edge wires @{Perceptron}s up
 * It contains a channel
 */
class WiringEdge private (val weight : Double, val channel : Subject[(Double, Double)]) extends InputEdge with OutputEdge{
  /**
   * Pushing the activation value from one perceptron to another
   */
  def push(activation : Double){
     channel.onNext(activation, weight)
  }
  
  /**
   * Listen to the channel to receive new activation values from a perceptron
   */
  def listen(f : ((Double, Double)) => Unit){
    channel.subscribe(f)
  }
  
  def merge(that: Observable[(Double, Double)]) : Observable[(Double, Double)] = {
    this.channel.merge(that)
  }
}
object WiringEdge{
	def apply(weight : Double): WiringEdge = new WiringEdge(weight, Subject[(Double, Double)]())
  def apply(): WiringEdge = new WiringEdge(0, Subject[(Double, Double)]())
}