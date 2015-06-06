package com.netcloud.perceptron

import java.util.concurrent.TimeUnit

import scala.concurrent.{Await, Promise, Future}
import scala.util.{Failure, Try, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.languageFeature.postfixOps

case class Edge(value: Future[Double], weight: Double)

class Perceptron {

  /**
   * These should be observables to be multi-layered
   * we need streams of information flowing through the net
   */
  val inputEdges: List[Edge] = List[Edge](Edge(Future {1}, -1.5), Edge(Future {0}, 1), Edge(Future {1}, 1))

  def activate() : Future[Double] = {
    val result = Promise[Double]()
    val inputs = Future sequence inputEdges.map(_.value)
    inputs.onComplete {
      case Success(results) =>
        val sig = Perceptron.sigmoid(inputEdges.map(e => e.weight * e.value.value.get.get).sum)
        result.complete(Try(sig))
      case Failure(ex) =>
        result.failure(ex)
    }
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
