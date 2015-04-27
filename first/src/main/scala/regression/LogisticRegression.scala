package regression

import java.awt.{Color, Paint}

import breeze.linalg._
import breeze.plot._
import util.MatrixUtil

import scala.annotation.tailrec
import scala.io.Source

/**
 * Created by goldratio on 4/27/15.
 */
class LogisticRegression(x: DenseMatrix[Double], y: DenseVector[Double], alpha: Double, numIterations: Int) {

  val epsilon: Double = 1e-8

  val m: Double =  y.length

  def start() = {
    val theta = DenseVector(0.000240, 0.000449, 0.000306)//DenseVector.rand(x.cols) * 0.001
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
    val h = MatrixUtil.sigmoid(x * theta)

    val h1 = (- y).t * MatrixUtil.log(h)
    val test: DenseVector[Double] = (-y) + 1.0

    val h2 = test.t * MatrixUtil.log((- h) + 1.0)
    val cost = (h1 - h2) / m

    val h3 = x.t * (h - y)

    (cost, h3 / m)
    //val h = x * theta - y //x * theta -y
    //val h1 = h.t * h // (h transpose)  * h
    //val cost = h1 / (2 * m)
    //val h2 = x.t * h
    //(cost, h2 / m)
  }

}

object LogisticRegression extends App {

  System.out.println("加载数据...\n")


  val matrix = DenseMatrix(
    Source.fromFile("data/logistic.txt").getLines().toList.flatMap { x =>
      val y = x.split("\t")
      Seq(
        (1.0, y(0).toDouble, y(1).toDouble, y(2).toDouble)
      )
    }: _*)

  val x = matrix(::, 0 to 2)

  val y = matrix(::, 3)
  val re = new LogisticRegression(x, y, 0.0024, 1500000)
  val result = re.start()
  println(result)


  val f = Figure()
  val i2color: Int => Paint = _ match {
    case 0 => Color.BLUE //accepted
    case 1 => Color.RED //rejected
    case 2 => Color.BLACK //other
    case _ => Color.YELLOW//other
  }
    val y2Color: DenseVector[Double] => (Int => Paint) = y => {
    case i => {
      i2color(y(i).toInt)
    }
  }

  val test = y2Color(y)
  f.subplot(0) += scatter(x(::, 1), x(::, 2), {_ => 0.1}, test)
  f.subplot(0).xlabel = "X-value"
  f.subplot(0).ylabel = "Y-value"
  f.subplot(0).title = "Input data"
  f.subplot(0).xlim(-5, 5)
  f.subplot(0).ylim(-3, 15)

  val lineX = linspace(-5 , 5)
  println(s"${- result(1) / result(2)}, ${- result(0) / result(2)}" )

  f.subplot(0)  += plot(lineX, lineX * ( - result(1) / result(2)) - result(0) / result(2))

  //f.subplot(0)  += plot(lineX, lineX * ( -0.48007329 / -0.6168482) -  4.12414349 / -0.6168482)

  println("\n\nTo finish this program, close the result window.")

}