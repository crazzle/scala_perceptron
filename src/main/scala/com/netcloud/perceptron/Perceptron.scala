package com.netcloud.perceptron

import com.netcloud.perceptron.Perceptron.Activatable
import rx.lang.scala.subjects.{SerializedSubject}

import scala.async.Async.async
import rx.lang.scala.{Subject}

import scala.concurrent.ExecutionContext

/**
 * A @{Perceptron} is the core component of a neural net.
 * It consists of multiple inputchannel and an outputchannel.
 * As soon as all inputs are defined it calculates the output value
 * and pushes it to the outputchannel that other perceptrons can use
 * as its input.
 */
case class Perceptron(inputs: Seq[Edge] = Seq.empty,
                      outputs: Seq[Edge] = Seq.empty,
                      f: (Double) => Double = Perceptron.sigmoid)
                     (implicit ec : ExecutionContext) extends Activatable {

  /**
    * Run at instantiation to initialize the perceptrons input-stream
    */
  init()

  /**
   * Initializes the perceptron by applying the right
   * function to all input edges
   */
  private def init(): Unit = {
    val merged = SerializedSubject(Subject[Double]())
    inputs.map(_.channel.map{case (activation, weight) => activation * weight})
      .foreach(_.subscribe(merged.onNext(_)))

    merged.scan((0d,0)){
      case ((sumValue, count), value) => {
        if(count < inputs.size)
          (sumValue+value, count + 1)
        else
          (0d,0)
      }
    }.subscribe { activations =>
        async {
          if (activations._2 == inputs.size) {
            activate(activations._1)
          }
        }
      }
  }

  /**
   * The activation function
   */
  override def activate(value: Double) : Double = {
    val activation = f(value)
    outputs.foreach(_.push(activation))
    activation
  }

  /**
    * Adds an inputedges and creates a new percepton
    */
  def addInput(edge : Edge) : Perceptron = addInput(Seq(edge))

  /**
    * Adds an inputedges and creates a new percepton
    */
  def addInput(edge : Seq[Edge]) : Perceptron = copy(inputs=inputs++edge)


  /**
    * Adds an outputedges and creates a new percepton
    */
  def addOutput(edge : Edge) : Perceptron = addOutput(Seq(edge))

  /**
    * Adds an outputedges and creates a new percepton
    */
  def addOutput(edge : Seq[Edge]) : Perceptron = Perceptron(outputs=outputs++edge)

}
object Perceptron {

  /**
    * Sigmoid function that is used during the activation
    */
  def sigmoid(value: Double): Double = {
    1 / (1 + Math.exp(-1 * value))
  }

  /**
    * A perceptron is an Activatable. That means it can be activated if it has
    * enough values pushed over the edges
    */
  trait Activatable {
    def activate(value: Double) : Double
  }
}
