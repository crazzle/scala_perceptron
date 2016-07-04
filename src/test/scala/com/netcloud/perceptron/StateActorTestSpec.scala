package com.netcloud.perceptron

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.netcloud.GlobalContext
import com.netcloud.training.{ActivationKeeper, CurrentActivation, GetActivation, NewActivation}
import org.scalatest.WordSpec
import scala.language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration._

class StateActorTestSpec extends WordSpec {
  implicit val timeout = Timeout(5 seconds)

  "A StateActor" when {
    "send an Activation" should {
      "should store the current value" in {
        val ref = GlobalContext.globalActorSystem.actorOf(Props[ActivationKeeper])
        ref ! NewActivation(25.0)
        val f = ref ? GetActivation
        val state = Await.result(f, timeout.duration).asInstanceOf[CurrentActivation]
        assert(state.activation.equals(25.0))

        ref ! NewActivation(10.0)
        val f2 = ref ? GetActivation
        val state2 = Await.result(f2, timeout.duration).asInstanceOf[CurrentActivation]
        assert(state2.activation.equals(10.0))
      }
    }
  }
}
