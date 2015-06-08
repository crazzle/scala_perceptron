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
}