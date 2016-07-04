package com.netcloud.perceptron

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.netcloud.GlobalContext
import com.netcloud.training.{ActivationKeeper, Activation, GetActivation, Expectation}
import org.scalatest.WordSpec
import scala.concurrent.Await
import scala.concurrent.duration._

class StateActorTestSpec extends WordSpec {
  implicit val timeout = Timeout(5.seconds)

  "A StateActor" when {
    "send an Activation" should {
      "should store the current value" in {
        val ref = GlobalContext.globalActorSystem.actorOf(Props[ActivationKeeper])
        ref ! Expectation(0)
        ref ! Activation(25.0)
        val f = ref ? GetActivation
        val state = Await.result(f, timeout.duration).asInstanceOf[Activation]
        assert(state.value.equals(25.0))

        ref ! Activation(10.0)
        val f2 = ref ? GetActivation
        val state2 = Await.result(f2, timeout.duration).asInstanceOf[Activation]
        assert(state2.value.equals(10.0))
      }
    }
  }
}
