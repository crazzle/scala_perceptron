package com.netcloud.perceptron

import akka.util.Timeout
import org.scalatest.WordSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import com.netcloud.training.{CurrentActivation, Trainer}

/**
  * Created by markkeinhorster on 07.01.16.
  */
class TrainerTestSpec extends WordSpec {
  implicit val timeout = Timeout(5 seconds)

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

        Thread.sleep(1000)
        val result = Await.result(p.getState, timeout.duration).asInstanceOf[CurrentActivation]
        assert(result.activation >= 0.5)
      }
    }
  }

  "A trainer" when {
    "be stackable" should {
      "should propose a new weight based on backpropagation" in {
        val statEdge = WiringEdge(-1.5)
        val edge1 = WiringEdge(1)
        val edge2 = WiringEdge(1)
        val ins = List[InputEdge](statEdge, edge1, edge2)

        val out = WiringEdge()
        val p : Trainer = new Perceptron("p", ins, out) with Trainer

        statEdge.push(1)
        edge1.push(1)
        edge2.push(1)

        Thread.sleep(1000)
        val result = Await.result(p.getState, timeout.duration).asInstanceOf[CurrentActivation]
        val error = 0.5
        val wire = p.backpropagate(error, result)
      }
    }
  }
}
