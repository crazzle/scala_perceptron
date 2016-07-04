package com.netcloud.training

import akka.actor.Actor

/**
  * Helps during the training phase to keep the last activation in a
  * fully functional manner
  */
class ActivationKeeper extends Actor{

  override def receive : Receive = {
    case Activation(value) => context.become(receiveWithActivation(value))
  }

  def receiveWithActivation(activation : Double) : Receive = {
    case GetActivation => sender ! Activation(activation)
    case Activation(value) => context.become(receiveWithActivation(value))
  }

}

case object GetActivation

case class Activation(value : Double)
