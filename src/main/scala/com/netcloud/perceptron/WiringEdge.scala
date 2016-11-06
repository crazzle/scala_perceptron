package com.netcloud.perceptron

import rx.lang.scala.Subject

import scala.async.Async.async
import scala.concurrent.ExecutionContext

/**
 * Abstraction for an edge, that contains a weight
 */
trait Edge{
  val channel : Subject[(Double, Double)]
  val weight : Double
  def listen(f : ((Double,Double)) => Unit) : Unit
  def push(activation : Double) : Unit
}


/**
 * An edge wires @{Perceptron}s up
 * It contains a channel and a weight
 * <<- It is a case class to use the copy function ->>
 */
case class WiringEdge private (weight : Double, channel : Subject[(Double, Double)])
                              (implicit ec : ExecutionContext) extends Edge{

  /**
   * Pushing the activation value from one perceptron to another
   */
  def push(activation : Double) : Unit = {
    async {
      channel.onNext((activation, weight))
    }
  }

  /**
   * Listen to the channel to receive new activation values from a perceptron
   */
  def listen(f : ((Double, Double)) => Unit) : Unit = {
    channel.subscribe(f)
  }

}
object WiringEdge{
  def apply(weight : Double = math.random)(implicit ec : ExecutionContext)
           : WiringEdge = new WiringEdge(weight, Subject[(Double, Double)]())
}
