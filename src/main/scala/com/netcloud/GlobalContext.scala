package com.netcloud

import akka.actor.ActorSystem

/**
  * A global singleton that keeps all the global components like the actor system
  */
object GlobalContext {
    val globalActorSystem = ActorSystem("PerceptronStateSystem")
}
