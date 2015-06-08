package com.netcloud.perceptron

import org.scalatest.WordSpec

/**
 * @author mark
 */
class EdgeTestSpec extends WordSpec{
  "An edge" when {
    "activation pushed" should {
      "should push to function" in {
        val edge = new WiringEdge()
        edge.listen { act => assert(act == 1) }
        edge.push(1)
      }
    }
  }
}