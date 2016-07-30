package com.netcloud.training

import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.netcloud.GlobalContext.globalActorSystem
import com.netcloud.perceptron.Perceptron.Activatable

import scala.concurrent.duration._

/**
  * Trait that is stackable in order to enhance an Activatable to keep track of the last set activation
  * using an actor to store the activation value
  */
trait Trainer extends Activatable {

  implicit val timeout = Timeout(5.seconds)

  val memory = globalActorSystem.actorOf(Props[Memory])

  def getActivation = memory ? GetActivation

  abstract override def activate(values: Seq[(Double, Double)]): Double = {
    val activation = super.activate(values)
    memory ! Activation(activation)
    activation
  }

}

/**
  * Helps during the training phase to keep the last activation in a
  * fully functional manner
  */
class Memory extends Actor{

  override def receive : Receive = {
    case GetActivation => sender ! IncompleteInputs
    case Activation(value) => context.become(receiveWithActivation(value))
  }

  def receiveWithActivation(activation : Double) : Receive = {
    case GetActivation => sender ! Activation(activation)
    case Activation(value) => context.become(receiveWithActivation(value))
  }

}

case object GetActivation

case class Activation(value : Double)

case object IncompleteInputs
