package regression

import breeze.linalg.{DenseVector, DenseMatrix}

import scala.annotation.tailrec
import scala.io.Source

/**
 * Created by goldratio on 4/25/15.
 */
class LinearRegression(x: DenseMatrix[Double], y: DenseVector[Double], alpha: Double, numIterations: Int) {
  val epsilon: Double = 1e-6

  val m: Double =  y.length

  def start() = {
    val theta = DenseVector.rand(x.cols) * 0.001
    this.train(theta, 0, 0)
  }

  @tailrec
  private def train(theta: DenseVector[Double], cost0: Double, index: Int): DenseVector[Double] = {
    val (cost, gradient) = this.compute(theta)
    val result = theta - (gradient * alpha)
    val step = Math.abs(cost0 - cost)
    if (step < epsilon || index > numIterations) {
      result
    }
    else {
      train(result, cost, index + 1)
    }
  }

  def compute(theta: DenseVector[Double]): (Double, DenseVector[Double] ) = {
    val h = x * theta - y //x * theta -y

    val h1 = h.t * h // (h transpose)  * h
    val cost = h1 / (2 * m)

    val h2 = x.t * h
    (cost, h2 / m)
  }

}


object LinearRegression extends App {

  System.out.println("加载数据...\n")


  val matrix = DenseMatrix(
    Source.fromFile("data/line.txt").getLines().toList.flatMap { x =>
      val y = x.split(",")
      Seq(
        (1.0, y(0).toDouble, y(1).toDouble)
      )
    }: _*)

  val x = matrix(::, 0 to 1)

  val y = matrix(::, 2)
  val re = new LinearRegression(x, y, 0.001, 1500)
  val result = re.start()
  println(result)

}