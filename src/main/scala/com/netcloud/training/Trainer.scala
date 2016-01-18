package com.netcloud.training

import akka.actor.Props
import akka.util.Timeout
import com.netcloud.GlobalContext
import com.netcloud.perceptron.Perceptron.Activatable
import GlobalContext.globalActorSystem
import akka.pattern.ask
import com.netcloud.training.StateActor.{NewActivation, GetState}
import scala.concurrent.duration._

/**
  * Trait that is stackable in order to enhance an Activatable to keep track of the last set activation
  * using an actor to store the activation value
  */
trait Trainer extends Activatable {
  /**
    * Timeout has to be for the ask pattern
    */
  implicit val timeout = Timeout(5 seconds)

  /**
    * Ref to the actor that keeps the Activatable's state
    */
  val keeper = globalActorSystem.actorOf(Props[StateActor])

  /**
    * Backpropagation edge
    */
  val backpropagation = ??? //Bind the inputchannels to the backpropagation edge in order to adapt the error

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
  def getState() = keeper ? GetState
}