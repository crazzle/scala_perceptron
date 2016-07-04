package com.netcloud.training

import akka.actor.Actor

/**
  * Helps during the training phase to keep the last activation in a
  * fully functional manner
  */
class ActivationKeeper extends Actor{

  override def receive : Receive = {
    case Expectation(value) => context.become(receiveWithExcpectation(value))
  }

  /**
    * Initial receive that is able to store the current activation of an perceptron
    */
  def receiveWithExcpectation(expectation : Double) : Receive = {
    case Activation(value) => context.become(receiveWithActivationAndExpectation(value, expectation))
  }

  /**
    * Receive that is in use after an initial activation.
    * It provides access to the current value that was the
    * result of the activation
    */
  def receiveWithActivationAndExpectation(activation : Double, expectation : Double) : Receive = {
    case Activation(value) => context.become(receiveWithActivationAndExpectation(value, expectation))
    case Expectation(value) => context.become(receiveWithExcpectation(value))
    case GetActivation => sender ! Activation(activation)
    case GetExpectation => sender ! Expectation(expectation)
  }
}

/**
  * Message to set the current expectation
  */
case class Expectation(value : Double)

/**
  * Message to ask for the current expectation
  */
case object GetExpectation


/**
  * Message that can be asked in order to return the current state of the perceptron
  */
case object GetActivation

/**
  * Message to send the current activation value of the perceptron
  */
case class Activation(value : Double)
