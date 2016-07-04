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

  implicit val timeout = Timeout(5.seconds)

  val keeper = globalActorSystem.actorOf(Props[ActivationKeeper])

  def getActivation = keeper ? GetActivation

  abstract override def activate(values: Seq[(Double, Double)]): Double = {
    val activation = super.activate(values)
    keeper ! Activation(activation)
    activation
  }

}
