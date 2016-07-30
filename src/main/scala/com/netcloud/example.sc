import com.netcloud.perceptron.{InputEdge, Perceptron, WiringEdge}

import scala.async.Async
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

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

val p = Promise[Double]()
outLast.listen {
  case (activation, weight) => p.success(activation)
}

statEdge1.push(1)
statEdge2.push(1)
statEdgeLast.push(1)
edgeA1.push(1)
edgeA2.push(1)
edgeB1.push(1)
edgeB2.push(1)


Await.result(p.future,Duration.Inf)
