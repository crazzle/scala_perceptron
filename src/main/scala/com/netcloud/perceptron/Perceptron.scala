package com.netcloud.perceptron

import java.util.concurrent.TimeUnit

import scala.concurrent.{Await, Promise, Future}
import scala.util.{Failure, Try, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.languageFeature.postfixOps

/**
 * A @{Perceptron} is the core component of a neural net.
 * It consists of multiple inputchannel and multiple outputchannel.
 * As soon as all inputs are defined it calculates the output value
 * and broadcasts it to all outputchannel.
 */
class Perceptron {

  /**
   * Edges a perceptron receives activation values from
   */
  var inputEdges: List[InputEdge] = List[InputEdge]()
  
  /**
   * Edges a perceptron broadcasts its activation value to
   */
  var outputEdges: List[OutputEdge] = List[OutputEdge]()

  /**
   * The activation function
   */
  def activate() : Future[Double] = {
    val result = Promise[Double]()
    //val inputs = Future sequence inputEdges.map(_.value)
    /*inputs.onComplete {
      case Success(results) =>
        val sig = Perceptron.sigmoid(inputEdges.map(e => e.weight * e.value.value.get.get).sum)
        result.complete(Try(sig))
      case Failure(ex) =>
        result.failure(ex)
    }*/
    result.future
  }

  /** Continues the computation of this future by taking the current future
    *  and mapping it into another future.
    *
    *  The function `cont` is called only after the current future completes.
    *  The resulting future contains a value returned by `cont`.
    */
  def continueWith[T, S](base: Future[T], cont: Future[T] => S): Future[S] = {
    val p = Promise[S]()
    base.onComplete{
      case _ => p.complete(Try(cont(base)))
    }
    p.future
  }
}

object Perceptron {
  def sigmoid(value: Double): Double = {
    1 / (1 + Math.exp(-1 * value))
  }

  def main(args: Array[String]) {
    val p = new Perceptron()
    val f = p.activate()
    Await.result(f, Duration(3, TimeUnit.SECONDS))
    println(f.value.get.get)
  }
}
