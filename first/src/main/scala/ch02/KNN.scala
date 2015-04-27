package ch02

import breeze.linalg._

import scala.io.Source

/**
 * Created by goldratio on 1/23/15.
 */
object KNN extends App {

  def createDataSet(): (Seq[(Double, Double)], Seq[Char]) = {
    val group = Seq[(Double, Double)]((1.0, 1.1), (1.0, 1.0), (0, 0), (0, 0.1))
    val labels = Seq('A', 'A', 'B', 'B')
    (group, labels)
  }

  def classify0(inX: Transpose[DenseVector[Double]], dataSet: DenseMatrix[Double], labelPosition: Int, k: Int) = {
    val rowNumber = dataSet.rows
    val result = (0 until rowNumber).map { i =>
      val row = dataSet(i, ::)
      (distance(inX, row), row(labelPosition))
    }.sortWith((a, b) => a._1 < b._1).take(k).groupBy(_._2).map(t => (t._1, t._2.length))
    result.foldLeft((0.0,0)) { (a, b) =>
      if(a._2 < b._2)
        b
      else
        a
    }._1

//    dataSet.zip(labels).map {
//      case (end, label) =>
//        (distance(inX, end), label)
//    }.sortWith((a, b) => a._1 < b._1).take(k)
  }

  def distance(start: Transpose[DenseVector[Double]], end: Transpose[DenseVector[Double]]) = {
    val size = start.inner.activeSize
    (0 until size).map { i =>
      Math.pow(start(i) - end(i), 2)
    }.foldLeft(0.0)(_ + _)
  }


  def autoNorm(dataSet: DenseMatrix[Double]): DenseMatrix[Double] = {
    val colNum = dataSet.cols
    val normal = DenseMatrix.zeros[Double](dataSet.rows, dataSet.cols)
    (0 until colNum).map { i =>
      val column = dataSet(::, i)
      val minValue = min(column)
      val maxValue = max(column)
      normal(::, i) := column.map { x =>
        (x - minValue) / (maxValue - minValue)
      }
    }
    normal
  }
//
//  def autoNorm(dataSet: DenseVector[Double]): DenseVector[Double] = {
//    val minValue = min(dataSet)
//    val maxValue = max(dataSet)
//    dataSet.map { x =>
//      (x - minValue) / (maxValue - minValue)
//    }
//  }

//  def distance(start: (Double, Double), end: (Double, Double)): Double = {
//    val d = Math.pow(start._1 - end._1, 2) + Math.pow(start._2 - end._2, 2)
//    Math.sqrt(d)
//  }

  //val (group, labels) = createDataSet()
  //classify0((0, 0), group, labels, 3)

  val likeMap = Map[String, Double] ("largeDoses" -> 1, "smallDoses" -> 2, "didntLike" -> 3)

  val data = DenseMatrix(
    Source.fromFile("data/datingTestSet.txt").getLines().toList.flatMap { x =>
      val y = x.split("\t")
      Seq(
        (y(0).toDouble, y(1).toDouble, y(2).toDouble, likeMap.get(y(3)).getOrElse(3.0))
      )
    }: _*)

  val X = data(::, 0 to 3)
  val normaX = autoNorm(X)
  val y = normaX(0 to 100, 0 to 2)

  var errorCount = 0.0
  (0 until y.rows).map { i =>
    val label = classify0(y(i, ::), normaX, 3, 3) * 2 + 1
    val realLabel = X(i, 3)
    if(label != realLabel)
      errorCount += 1
    println(s"the classifier came back with: $label, the real answer is $realLabel")
  }
  println(s"the total error rate is: ${errorCount / y.rows}")


//  val f = Figure()
//  val i2color: Int => Paint = _ match {
//    case 0 => Color.BLUE //accepted
//    case 1 => Color.RED //rejected
//    case 2 => Color.BLACK //other
//    case _ => Color.YELLOW//other
//  }
//
//  val y2Color: DenseVector[Double] => (Int => Paint) = y => {
//    case i => {
//      println(i2color(y(i).toInt))
//      i2color(y(i).toInt)
//    }
//  }
//
//  val test = y2Color(y)
//  f.subplot(0) += scatter(X(::, 0), X(::, 1), {_ => 0.3}, test)
//  f.subplot(0).xlabel = "X-value"
//  f.subplot(0).ylabel = "Y-value"
//  f.subplot(0).title = "Input data"
//  f.subplot(0).xlim(-1, 25)
//  f.subplot(0).ylim(-0.05, 2)
//
//  println("\n\nTo finish this program, close the result window.")


}
