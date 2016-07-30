package com.netcloud.perceptron

import org.scalatest.WordSpec

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

class EdgeTestSpec extends WordSpec {
  "An edge" when {
    "activation pushed" should {
      "push to function" in {
        val res = Promise[Boolean]()
        val edge = WiringEdge(1)
        edge.listen { act => res.success(act == (1, 1)) }
        edge.push((1))
        val result = Await.result(res.future,Duration.Inf)
        assert(result)
      }
    }
  }
}
