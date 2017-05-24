package com.netcloud.perceptron

import org.scalatest.WordSpec
import scala.concurrent.ExecutionContext.Implicits.global
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

        val out = Seq(WiringEdge())
        val res = Promise[Boolean]()
        out.foreach(_.listen {
          case (activation, weight) => res.success(activation >= 0.5)
        })
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

        val out = Seq(WiringEdge())
        val res = Promise[Boolean]()
        out.foreach(_.listen {
          case (activation, weight) => res.success(activation < 0.5)
        })

        Perceptron(ins, out)

        statEdge.push(1)
        edge1.push(1)
        edge2.push(0)
        val result = Await.result(res.future, Duration.Inf)
        assert(result)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as TRUE" in {
        val (inputA, inputB, output) = buildXORMultiLayeredPerceptron()

        val res = Promise[Boolean]()
        output.listen {
          case (activation, weight) => {
            res.success(activation == 1)
          }
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
      "properly recognize an XOR as FALSE when both is true" in {
        val (inputA, inputB, output) = buildXORMultiLayeredPerceptron()

        val res = Promise[Boolean]()
        output.listen {
          case (activation, weight) =>
            {
              res.success(activation == 0)
            }
        }

        inputA.push(1)
        inputB.push(1)
        val result = Await.result(res.future, Duration.Inf)
        assert(result)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as FALSE when both is false" in {
        val (inputA, inputB, out) = buildXORMultiLayeredPerceptron()

        val res = Promise[Boolean]()
        out.listen {
          case (activation, weight) => res.success(activation == 0)
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
    /**
     * Inputs for the neural network
     */
    val inputA = WiringEdge(1)
    val inputB = WiringEdge(1)

    /**
     * 1st Layer
     */
    val hidden1 = WiringEdge(1)
    Perceptron(Seq(inputA), Seq(hidden1), {case d => if(d == 1) 1d else 0d})

    val hidden2 = WiringEdge(-2)
    Perceptron(Seq(inputA, inputB), Seq(hidden2), {case d => if(d == 2) 1d else 0d})

    val hidden3 = WiringEdge(1)
    Perceptron(Seq(inputB), Seq(hidden3), {case d => if(d == 1) 1d else 0d})

    /**
     * Last Layer (output layer)
     */
    val hiddenOutput = List[Edge](hidden1, hidden2, hidden3)
    val out = WiringEdge(1)
    Perceptron(hiddenOutput, Seq(out), (d: Double) => d)

    (inputA, inputB, out)
  }
}