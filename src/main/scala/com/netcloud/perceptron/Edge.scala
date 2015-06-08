package com.netcloud.perceptron

import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.Subject


/**
 * An edge wires @{Perceptron}s up
 * It contains a channel
 */
class Edge {
  var channel : Subject[Double] = Subject[Double]()
  
  def push(activation : Double){
    channel.onNext(activation)
  }
  
  def listen(f : Double => Unit){
    channel.subscribe(f)
  }
}