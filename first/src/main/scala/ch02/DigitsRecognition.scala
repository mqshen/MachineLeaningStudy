package ch02

import java.io.File

import breeze.linalg.{DenseMatrix, DenseVector, Transpose}

import scala.io.Source

/**
 * Created by goldratio on 1/26/15.
 */
object DigitsRecognition extends App {

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

  def getFileMatrix(path: String): DenseMatrix[Double] = {
    val trainDict = new File(path)
    val trainFiles = trainDict.listFiles()

    val trainSet = DenseMatrix.zeros[Double](trainFiles.size, 1025)
    //val labelVector = DenseVector.zeros[Double](trainFiles.size)

    trainFiles.zipWithIndex.foreach { case(file, index) =>
      val fileName = file.getName
      val chats: Seq[Double] = Source.fromFile(file).getLines().foldLeft("")(_.concat(_)).map(_.toDouble)
      val test = DenseVector((chats :+ fileName.charAt(0).toDouble): _*)
      trainSet(index, ::) := Transpose(test)
    }
    trainSet

  }

  val dataSet = getFileMatrix("data/digits/trainingDigits")

  val testSet = getFileMatrix("data/digits/testDigits")

  var errorCount = 0.0
  (0 until testSet.rows).map { i =>
    val label = classify0(testSet(i, 0 to 1023), dataSet, 1024, 3)
    val realLabel = testSet(i, 1024)
    if(label != realLabel)
      errorCount += 1
    println(s"the classifier came back with: $label, the real answer is $realLabel")
  }
  println(s"the total error rate is: ${errorCount / testSet.rows}")
}