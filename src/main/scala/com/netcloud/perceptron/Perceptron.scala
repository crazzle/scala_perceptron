package com.netcloud.perceptron

import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, Promise, Future}
import scala.util.{Failure, Try, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.languageFeature.postfixOps

/**
 * A @{Perceptron} is the core component of a neural net.
 * It consists of multiple inputchannel and multiple outputchannel.
 * As soon as all inputs are defined it calculates the output value
 * and broadcasts it to all outputchannel.
 */
class Perceptron(val ins : List[InputEdge], val outs : List[OutputEdge]) {

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
  private[this] type ActivationWithWeight = (Double, Double)
  private[this] var activations = Vector[ActivationWithWeight]()
  
  /**
   * Initializes the perceptron by applying the right
   * function to all input edges
   */
  def init() : Unit = {
    inputEdges.foreach { in => in.listen(listen) }
  }
  
  /**
   * Listens for new activations and if all ready activates
   * the perceptron
   */
  private[this] def listen : ((Double,Double)) => Unit = { activationWithWeight =>
    synchronized {
      activations = activations :+ activationWithWeight
      if(activations.size == inputEdges.size){
        activate
        activations = Vector[ActivationWithWeight]()
      }
    }
  }
  
  /**
   * The activation function
   */
  private[this] def activate() : Unit = {
    val act = Perceptron.sigmoid(activations.map(_ match {
      case (activation, weight) => activation * weight
      case _ => throw new IllegalArgumentException("That was not an ActivationWithWeight")
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
  
  def sigmoid(value: Double): Double = {
    1 / (1 + Math.exp(-1 * value))
  }

  def main(args: Array[String]) {
    /**
     * Set up the edges
     */
    val statEdge = new WiringEdge()
    statEdge.weight = -1.5
    val edge1 = new WiringEdge()
    edge1.weight = 1
    val edge2 = new WiringEdge()
    edge2.weight = 1
    val ins = List[InputEdge](statEdge, edge1, edge2)
    
    /**
     * Set up output edge
     */
    val out = new WiringEdge()
    out.listen {tuple => tuple match {
        case (activation, weight) => println("Is a logic AND: " + (activation>0.5))
      }
    }
    val outs = List[OutputEdge](out)
    
    /**
     * Wire up perceptrons
     */
    val p = new Perceptron(ins, outs)
    p.init()
    
    /**
     * Push a TRUE through the edges
     */
    statEdge.push(1)
    edge1.push(1)
    edge2.push(1)
    
    /**
     * Push a FALSE through the edges
     */
    statEdge.push(1)
    edge1.push(0)
    edge2.push(1)

    /**
     * Wait for output before finishing
     */
    Thread.sleep(1000)
  }
}
