package com.netcloud.perceptron

import org.scalatest.WordSpec

class PerceptronTestSpec extends WordSpec {
  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an AND as TRUE" in {
        val statEdge = WiringEdge(-1.5)
        val edge1 = WiringEdge(1)
        val edge2 = WiringEdge(1)
        val ins = List[InputEdge](statEdge, edge1, edge2)

        val out = WiringEdge()
        var res: Boolean = false
        out.listen {
          case (activation, weight) => res = activation >= 0.5
        }
        Perceptron("p", ins, out)

        statEdge.push(1)
        edge1.push(1)
        edge2.push(1)
        Thread.sleep(1000)
        assert(res)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an AND as FALSE" in {
        val statEdge = WiringEdge(-1.5)
        val edge1 = WiringEdge(1)
        val edge2 = WiringEdge(1)
        val ins = List[InputEdge](statEdge, edge1, edge2)

        val out = WiringEdge()
        var res: Boolean = false
        out.listen {
          case (activation, weight) => res = activation < 0.5
        }

        Perceptron("p", ins, out)

        statEdge.push(1)
        edge1.push(1)
        edge2.push(0)
        Thread.sleep(1000)
        assert(res)

        res = false
        statEdge.push(1)
        edge1.push(0)
        edge2.push(1)
        Thread.sleep(1000)
        assert(res)

        res = false
        statEdge.push(1)
        edge1.push(0)
        edge2.push(0)
        Thread.sleep(1000)
        assert(res)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as TRUE" in {
        val (edgeA1, edgeA2, edgeB1, edgeB2, statEdge1, statEdge2, statEdgeLast, outLast) = buildXORMultiLayeredPerceptron()

        var res = false
        outLast.listen {
          case (activation, weight) => res = activation > 0
        }

        statEdge1.push(1)
        statEdge2.push(1)
        statEdgeLast.push(1)
        edgeA1.push(0)
        edgeA2.push(0)
        edgeB1.push(1)
        edgeB2.push(1)
        Thread.sleep(1000)
        assert(res)

        res = false
        statEdge1.push(1)
        statEdge2.push(1)
        statEdgeLast.push(1)
        edgeA1.push(1)
        edgeA2.push(1)
        edgeB1.push(0)
        edgeB2.push(0)
        Thread.sleep(1000)
        assert(res)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as FALSE" in {
        val (edgeA1, edgeA2, edgeB1, edgeB2, statEdge1, statEdge2, statEdgeLast, outLast) = buildXORMultiLayeredPerceptron()

        var res = false
        outLast.listen {
          case (activation, weight) => res = activation < 0.5
        }
        statEdge1.push(1)
        statEdge2.push(1)
        statEdgeLast.push(1)
        edgeA1.push(1)
        edgeA2.push(1)
        edgeB1.push(1)
        edgeB2.push(1)
        Thread.sleep(1000)
        assert(res)
        res = false

        statEdge1.push(1)
        statEdge2.push(1)
        statEdgeLast.push(1)
        edgeA1.push(0)
        edgeA2.push(0)
        edgeB1.push(0)
        edgeB2.push(0)
        Thread.sleep(1000)
        assert(res)
      }
    }
  }

  /**
   * Wires a multilayered perceptron up
   */
  def buildXORMultiLayeredPerceptron(): (WiringEdge, WiringEdge, WiringEdge, WiringEdge, WiringEdge, WiringEdge, WiringEdge, WiringEdge) = {
    // P1 input
    val edgeA1 = WiringEdge(1)
    val edgeA2 = WiringEdge(-1)
    val statEdge1 = WiringEdge(-0.5)

    // P2 input
    val edgeB1 = WiringEdge(-1)
    val edgeB2 = WiringEdge(1)
    val statEdge2 = WiringEdge(-0.5)

    //
    val edgeLast1 = WiringEdge(1)
    val edgeLast2 = WiringEdge(1)
    val statEdgeLast = WiringEdge(-0.5)

    /**
     * 1st Layer
     */
    // First Perceptron
    val a = List[InputEdge](edgeA1, edgeB1, statEdge1)
    val out1 = WiringEdge()
    out1.listen {
      case (activation, weight) => edgeLast1.push(if (activation > 0) 1 else 0)
    }
    Perceptron("p1", a, out1, (d: Double) => d)

    // Second Perceptron
    val b = List[InputEdge](edgeB2, edgeA2, statEdge2)
    val out2 = WiringEdge()
    out2.listen {
      case (activation, weight) => edgeLast2.push(if (activation > 0) 1 else 0)
    }
    Perceptron("p2", b, out2, (d: Double) => d)

    /**
     * Last Layer (output layer)
     */
    val insLast = List[InputEdge](edgeLast1, edgeLast2, statEdgeLast)
    val outLast = WiringEdge()
    Perceptron("pLast", insLast, outLast, (d: Double) => d)

    (edgeA1, edgeA2, edgeB1, edgeB2, statEdge1, statEdge2, statEdgeLast, outLast)
  }
}