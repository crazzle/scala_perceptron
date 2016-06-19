package com.netcloud.training

import akka.actor.Actor
import com.netcloud.training.StateActor.{NewActivation, GetState, State}

/**
  * Helps during the training phase to keep the last activation in a
  * fully functional manner
  */
class StateActor extends Actor{

  /**
    * Initial receive that is able to store the current activation of an perceptron
    */
  override def receive = {
    case NewActivation(value) => context.become(receiveWithState(value))
  }

  /**
    * Receive that is in use after an initial activation.
    * It provides access to the current value that was the
    * result of the activation
    */
  def receiveWithState(activation : Double) : Receive = {
    case NewActivation(value) => context.become(receiveWithState(value))
    case GetState => sender ! State(activation)
  }
}
object StateActor{

  /**
    * Message that can be asked in order to return the current state of the perceptron
    */
  case object GetState

  /**
    * Message to send the current activation value of the perceptron
    */
  case class NewActivation(value : Double)

  /**
    * Current state of the perceptron that gets sent as an answer
    */
  case class State(activation : Double)
}