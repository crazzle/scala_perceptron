package com.netcloud.perceptron

import org.scalatest.WordSpec

class EdgeTestSpec extends WordSpec {
  "An edge" when {
    "activation pushed" should {
      "push to function" in {
        var res = false
        val edge = WiringEdge(1)
        edge.listen { act => res = act == (1, 1) }
        edge.push((1))
        Thread.sleep(1000)
        assert(res)
      }
    }

    "multiple activations pushed" should {
      "push to function" in {
        var res = false
        val edge = WiringEdge(1)
        edge.listen { act => res = act == (1, 1) }
        edge.push((1))
        Thread.sleep(1000)
        assert(res)

        res = false
        edge.push((1))
        Thread.sleep(1000)
        assert(res)

      }
    }
  }
}