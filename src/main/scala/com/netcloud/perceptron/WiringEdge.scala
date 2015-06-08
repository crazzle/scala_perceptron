package com.netcloud.perceptron

import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subject

/**
 * Abstraction for an edge, that contains a weight
 */
trait Edge{
  var weight : Double
}

/**
 * Abstraction for edges acting as an input to a perceptron
 */
trait InputEdge extends Edge{
  def listen(f : Double => Unit)
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
class WiringEdge extends InputEdge with OutputEdge{
  
  /**
   * The weight, has to be mutable concerning backtracking
   */
  var weight = 1.0
  
  /**
   * The channel used to move information from one perceptron to another
   */
  private[this] val channel : Subject[Double] = Subject[Double]()
  
  /**
   * Pushing the activation value from one perceptron to another
   */
  def push(activation : Double){
    channel.onNext(activation)
  }
  
  /**
   * Listen to the channel to receive new activation values from a perceptron
   */
  def listen(f : Double => Unit){
    channel.subscribe(f)
  }
}