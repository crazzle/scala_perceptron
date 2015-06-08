package com.netcloud.perceptron

import org.scalatest.WordSpec

class EdgeTestSpec extends WordSpec {
  "An edge" when {
    "activation pushed" should {
      "push to function" in {
        val edge = WiringEdge(1)
        edge.listen { act => assert(act == (1, 1)) }
        edge.push((1))
      }
    }
  }
}