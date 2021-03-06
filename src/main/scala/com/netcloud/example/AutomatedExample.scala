package com.netcloud.example

import java.io.{BufferedReader, InputStreamReader}
import java.util.concurrent.Executors

import akka.dispatch.ExecutionContexts
import com.netcloud.perceptron.{Perceptron, WiringEdge}
import scala.language.postfixOps
import scala.util.Random

object AutomatedExample extends App {

  implicit val ec = ExecutionContexts.fromExecutor(Executors.newFixedThreadPool(6))

  val numInputs : Int = 2
  val numHidden : Int  = 3
  val numOutputs : Int  = 1

  val r = new Random()

  val inputEdges = Seq.fill(numOutputs)(WiringEdge(1.0))
  val inputA = new Perceptron(inputs = Seq(WiringEdge(1.0)))
  val inputB = new Perceptron(inputs = Seq(WiringEdge(1.0)))

  //val outputEdges = Seq.fill(layer2Size)(WiringEdge(1.0)(wec))
  /**
    * Output is 1 outputedge of layer 2 with weight of 1
    */
  val outputInputEdges = Seq.fill(numHidden)(WiringEdge(1.0))
  val outputOutputEdge = WiringEdge(1.0)
  val output = new Perceptron().addInput(outputInputEdges)

  /*val resultEdges = Seq.fill(numOutputs)(WiringEdge(1.0)(wec))
  val layer2InputEdges = (for {
    p <- 1 to numOutputs*layer1Size
  }yield(WiringEdge(r.nextFloat())(wec)))
  val outputPerceptron = new Perceptron(layer2InputEdges, resultEdges)(pec)

  /*val layer1perceptrons = for{
    layer1 <- layer1InputEdges
    out <- layer1._2
  } yield new Perceptron(inputs.flatMap(_.outputs), Seq(WiringEdge(r.nextFloat())(wec)))(pec) with Trainer

  /**
    * Inputs are 2 Perceptrons with 1 input edge each with a weight of 1
    */
  val inputEdges = Seq.fill(numInputs)(WiringEdge(1.0)(wec))
  val inputs = for(input <- inputEdges)
    yield new Perceptron(Seq(input), Seq(WiringEdge(r.nextFloat())(wec)))(pec) with Trainer

  /**
    * Layer 1 input edges = number of inputs * number of layer1 perceptrons
    */
  val layer1InputEdges = (for {
    p <- 1 to numInputs
    o <- 1 to layer1Size
  }yield(p -> WiringEdge(r.nextFloat())(wec))).groupBy(_._1).mapValues(_.map(_._2))

  val layer1perceptrons = for(layer1 <- layer1InputEdges; out <- layer1._2)
    yield new Perceptron(inputs.flatMap(_.outputs), Seq(WiringEdge(r.nextFloat())(wec)))(pec) with Trainer


  /**
    * Output is 1 outputedge of layer 2 with weight of 1
    */
  val outputEdges = Seq.fill(layer2Size)(WiringEdge(1.0)(wec))

  /**
    * All Edges are created, now they have to be wired up
    */*/
  */

  println("press enter...")
  new BufferedReader(new InputStreamReader(System.in)).readLine()
  System.exit(0);
}
