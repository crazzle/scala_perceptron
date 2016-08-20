package com.netcloud.example

/**
  * Created by markkeinhorster on 20.08.16.
  */
object Example extends App {

  import com.netcloud.perceptron.{InputEdge, Perceptron, WiringEdge}
  import com.netcloud.training.Trainer
  import com.netcloud.GlobalContext._

  import scala.concurrent.{Await, Promise}
  import scala.concurrent.duration.Duration
  import scala.util.Random

  val s = globalActorSystem

  def randomEdge() = WiringEdge(Random.nextInt())

  // P1 input
  val edgeA1 = randomEdge()
  val edgeA2 = randomEdge()

  // P2 input
  val edgeB1 = randomEdge()
  val edgeB2 = randomEdge()

  // Input for output perceptron
  val edgeLast1 = randomEdge()
  val edgeLast2 = randomEdge()

  /**
    * 1st Layer
    */
  // First Perceptron
  val a = List[InputEdge](edgeA1, edgeB1)
  val out1 = WiringEdge()
  out1.listen {
    case (activation, weight) => edgeLast1.push(activation*weight)
  }
  val pA = new Perceptron("p1", a, out1) with Trainer

  // Second Perceptron
  val b = List[InputEdge](edgeB2, edgeA2)
  val out2 = WiringEdge()
  out2.listen {
    case (activation, weight) => edgeLast2.push(activation*weight)
  }
  val pB = new Perceptron("p2", b, out2) with Trainer

  /**
    * Last Layer (output layer)
    */
  val insLast = List[InputEdge](edgeLast1, edgeLast2)
  val outLast = WiringEdge()
  val pO = new Perceptron("pLast", insLast, outLast) with Trainer

  val p = Promise[Double]()
  outLast.listen {
    case (activation, weight) => p.success(activation)
  }

  edgeA1.push(1)
  edgeA2.push(1)
  edgeB1.push(1)
  edgeB2.push(1)


  val result = Await.result(p.future,Duration.Inf)

  print("the result is : " + result)





  System.exit(0)

}
