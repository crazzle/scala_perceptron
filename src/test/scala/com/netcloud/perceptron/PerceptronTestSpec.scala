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
        out.listen { tuple =>
          tuple match {
            case (activation, weight) => assert(activation >= (0.5))
          }
        }
        val outs = List[OutputEdge](out)

        val p = Perceptron(ins, outs)
        statEdge.push(1)
        edge1.push(1)
        edge2.push(1)
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
        out.listen { tuple =>
          tuple match {
            case (activation, weight) => assert(activation < (0.5))
          }
        }
        val outs = List[OutputEdge](out)

        val p = Perceptron(ins, outs)
        statEdge.push(1)
        edge1.push(0)
        edge2.push(1)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as TRUE" in {
        val (edge11, edge12, edge21, edge22, outLast) = buildXORMultiLayeredPerceptron()

        outLast.listen { tuple =>
          tuple match {
            case (activation, weight) => assert(activation >= (0.5))
          }
        }

        edge11.push(1)
        edge12.push(1)
        edge21.push(0)
        edge22.push(0)
        
        edge11.push(0)
        edge12.push(0)
        edge21.push(1)
        edge22.push(1)
      }
    }
  }

  "A perceptron" when {
    "correctly wired up" should {
      "properly recognize an XOR as FALSE" in {
        val (edge11, edge12, edge21, edge22, outLast) = buildXORMultiLayeredPerceptron()

        outLast.listen { tuple =>
          tuple match {
            case (activation, weight) => assert(activation < (0.5))
          }
        }

        edge11.push(1)
        edge12.push(1)
        edge21.push(1)
        edge22.push(1)
        
        edge11.push(0)
        edge12.push(0)
        edge21.push(0)
        edge22.push(0)
      }
    }
  }

  /**
   * Wires a multilayered perceptron up
   */
  def buildXORMultiLayeredPerceptron(): (WiringEdge, WiringEdge, WiringEdge, WiringEdge, WiringEdge) = {
    val edge11 = WiringEdge(0.6)
    val edge12 = WiringEdge(1.1)
    val edge21 = WiringEdge(1.1)
    val edge22 = WiringEdge(0.6)
    val edgeLast1 = WiringEdge(-1)
    val edgeLast2 = WiringEdge(1.1)

    /**
     * 1st Layer
     */
    // First Perceptron
    val ins1 = List[InputEdge](edge11, edge12)
    val out1 = WiringEdge()
    out1.listen { tuple =>
      tuple match {
        case (activation, weight) => edgeLast1.push(activation)
      }
    }
    val outs1 = List[OutputEdge](out1)
    val p1 = Perceptron(ins1, outs1)

    // Second Perceptron
    val ins2 = List[InputEdge](edge21, edge22)
    val out2 = WiringEdge()
    out2.listen { tuple =>
      tuple match {
        case (activation, weight) => edgeLast2.push(activation)
      }
    }
    val outs2 = List[OutputEdge](out2)
    val p2 = Perceptron(ins2, outs2)

    /**
     * Last Layer (output layer)
     */
    val insLast = List[InputEdge](edgeLast1, edgeLast2)
    val outLast = WiringEdge()
    val outsLast = List[OutputEdge](outLast)
    val pLast = Perceptron(insLast, outsLast)

    (edge11, edge12, edge21, edge22, outLast)
  }
}