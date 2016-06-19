package com.netcloud.perceptron

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.netcloud.GlobalContext
import com.netcloud.training.StateActor
import com.netcloud.training.StateActor.{State, GetState, NewActivation}
import org.scalatest.WordSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class StateActorTestSpec extends WordSpec {
  implicit val timeout = Timeout(5 seconds)

  "A StateActor" when {
    "send an Activation" should {
      "should store the current value" in {
        val ref = GlobalContext.globalActorSystem.actorOf(Props[StateActor])
        ref ! NewActivation(25.0)
        val f = ref ? GetState
        val state = Await.result(f, timeout.duration).asInstanceOf[State]
        assert(state.activation.equals(25.0))

        ref ! NewActivation(10.0)
        val f2 = ref ? GetState
        val state2 = Await.result(f2, timeout.duration).asInstanceOf[State]
        assert(state2.activation.equals(10.0))
      }
    }
  }
}