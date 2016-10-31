package com.netcloud.perceptron

import org.scalatest.WordSpec

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

class PerceptronTestSpec extends WordSpec {
  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an AND as TRUE" in {
        val statEdge = WiringEdge(-1.5)
        val edge1 = WiringEdge(1)
        val edge2 = WiringEdge(1)
        val ins = List[Edge](statEdge, edge1, edge2)

        val out = WiringEdge()
        val res = Promise[Boolean]()
        out.listen {
          case (activation, weight) => res.success(activation >= 0.5)
        }
        Perceptron(ins, out)

        statEdge.push(1)
        edge1.push(1)
        edge2.push(1)
        val result = Await.result(res.future,Duration.Inf)
        assert(result)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an AND as FALSE" in {
        val statEdge = WiringEdge(-1.5)
        val edge1 = WiringEdge(1)
        val edge2 = WiringEdge(1)
        val ins = List[Edge](statEdge, edge1, edge2)

        val out = WiringEdge()
        val res = Promise[Boolean]()
        out.listen {
          case (activation, weight) => res.success(activation < 0.5)
        }

        Perceptron(ins, out)

        statEdge.push(1)
        edge1.push(1)
        edge2.push(0)
        Thread.sleep(1000)
        val result = Await.result(res.future, Duration.Inf)
        assert(result)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as TRUE" in {
        val (inputA, inputB, outLast) = buildXORMultiLayeredPerceptron()

        val res = Promise[Boolean]()
        outLast.listen {
          case (activation, weight) => res.success(activation > 0)
        }

        inputA.push(1)
        inputB.push(0)
        val result = Await.result(res.future, Duration.Inf)
        assert(result)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as FALSE" in {
        val (inputA, inputB, outLast) = buildXORMultiLayeredPerceptron()

        val res = Promise[Boolean]()
        outLast.listen {
          case (activation, weight) => res.success(activation < 0.5)
        }
        inputA.push(0)
        inputB.push(0)
        val result = Await.result(res.future, Duration.Inf)
        assert(result)
      }
    }
  }

  /**
   * Wires a multilayered perceptron up
   */
  def buildXORMultiLayeredPerceptron(): (WiringEdge, WiringEdge, WiringEdge) = {
    // P1 input
    val inputA = WiringEdge(1)

    // P2 input
    val inputB = WiringEdge(1)

    //
    val edgeLast1 = WiringEdge(1)
    val edgeLast2 = WiringEdge(1)

    /**
     * 1st Layer
     */
    // First Perceptron
    val a = List[Edge](inputA, inputB)
    val out1 = WiringEdge()
    out1.listen {
      case (activation, weight) => edgeLast1.push(if (activation > 0) 1 else 0)
    }
    Perceptron(a, out1, (d: Double) => d)

    // Second Perceptron
    val b = List[Edge](inputA, inputB)
    val out2 = WiringEdge()
    out2.listen {
      case (activation, weight) => edgeLast2.push(if (activation > 0) 1 else 0)
    }
    Perceptron(b, out2, (d: Double) => d)

    /**
     * Last Layer (output layer)
     */
    val insLast = List[Edge](edgeLast1, edgeLast2)
    val outLast = WiringEdge()
    Perceptron(insLast, outLast, (d: Double) => d)

    (inputA, inputB, outLast)
  }
}