package com.netcloud.training

import akka.actor.Props
import akka.util.Timeout
import com.netcloud.GlobalContext
import com.netcloud.perceptron.Perceptron.Activatable
import GlobalContext.globalActorSystem
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Trait that is stackable in order to enhance an Activatable to keep track of the last set activation
  * using an actor to store the activation value
  */
trait Trainer extends Activatable {
  /**
    * Timeout has to be for the ask pattern
    */
  implicit val timeout = Timeout(5.seconds)

  /**
    * Ref to the actor that keeps the Activatable's state
    */
  val keeper = globalActorSystem.actorOf(Props[ActivationKeeper])

  /**
    * Backpropagation edge
    */
  def backpropagate(error : Double, state : CurrentActivation) = {
    channels.foreach(inputedge => inputedge.backfeed(state.activation - error))
  } //Bind the inputchannels to the backpropagation edge in order to adapt the error

  /**
    * Stacked activation function
    */
  abstract override def activate(values: Seq[(Double, Double)]): Double = {
    val activation = super.activate(values)
    keeper ! NewActivation(activation)
    activation
  }

  /**
    * Returns the current state of the Activatable
    */
  def getState = keeper ? GetActivation

  /*def error = keeper ? GetActivation map { case CurrentActivation(activation) => {
      channels.foldLeft(1D){
        case (acc, el) => acc + (activation * (1-activation) * (expected - activation))
      }
    }
  }*/

  /*
  Outputlayer backprop:
  error: activation * (1-activation) * (expected - activation)

  Hiddenlayer backprop:
  delta: activation * (1 -activation) * weight * error

   m_hl(i).getActivation * (1 - m_hl(i).getActivation) * m_ol(j).getWeights(i + 1) * olDeltas(j);

   */

}
