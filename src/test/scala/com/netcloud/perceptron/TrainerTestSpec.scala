package com.netcloud.perceptron

import org.scalatest.WordSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import com.netcloud.training.{Activation, IncompleteInputs, Trainer}

/**
  * Created by markkeinhorster on 07.01.16.
  */
class TrainerTestSpec extends WordSpec {
  "A trainer" when {
    "be stackable" should {
      "should store the current value" in {
        val statEdge = WiringEdge(-1.5)
        val edge1 = WiringEdge(1)
        val edge2 = WiringEdge(1)
        val ins = List[InputEdge](statEdge, edge1, edge2)

        val out = WiringEdge()
        val p : Trainer = new Perceptron("p", ins, out) with Trainer

        statEdge.push(1)
        edge1.push(1)
        edge2.push(1)

        val result = getActivation(p)
        assert(result.value >= 0.5)
      }
    }
  }

  def getActivation(p : Trainer) : Activation = {
    Await.result(p.getActivation, Duration.Inf) match {
      case IncompleteInputs => {
        getActivation(p)
      }
      case a : Activation => a
    }
  }
}
