package ch03

import breeze.linalg.{Transpose, DenseVector, DenseMatrix}

/**
 * Created by goldratio on 1/27/15.
 */
object DecideTree extends App {

  def calcShannonEnt(dataSet: DenseMatrix[Double]): Double = {
    val rowNumber = dataSet.rows
    val colNumber = dataSet.cols

    val labelCounts =  (0 until rowNumber).map { index =>
      dataSet(index, colNumber - 1)
    }.groupBy(l => l).map(t => (t._1, t._2.length))
    val entropy = labelCounts.foldLeft(0.0){ case (prob, (key, value)) =>
      val p = value.toDouble / rowNumber
      prob - p * Math.log(p)/Math.log(2)
    }
    entropy
  }

  def splitDataSet(dataSet: DenseMatrix[Double], axis: Int, value: Double) = {
    val list = scala.collection.mutable.ArrayBuffer[DenseVector[Double]]()
    val colNumber = dataSet.cols
    (0 until dataSet.rows).map { x =>
      val row = dataSet(x, ::)
      if(row(axis) == value) {
        val item = DenseVector.zeros[Double](colNumber - 1)
        if(axis > 0)
          item(0 until axis) := row.inner(0 until axis)
        if(axis < colNumber - 1)
          item(axis until (colNumber - 1)) := row.inner((axis + 1) until colNumber)
        list += item
      }
    }
    val reslut = DenseMatrix.zeros[Double](list.size, colNumber - 1)
    list.zipWithIndex.foreach { case (item, index) =>
      reslut(index, ::) := Transpose(item)
    }
    reslut
  }

  def chooseBestFeatureToSplit(dataSet: DenseMatrix[Double]): Int = {
    val numFeatures = dataSet.cols - 1
    val baseEntropy = calcShannonEnt(dataSet)
    var bestInfoGain = 0.0
    var bestFeature = -1
    (0 until numFeatures).foreach { index =>
      val featList = dataSet(::, index)
      val featSet = scala.collection.mutable.HashSet[Double]()
      featList.foreach(featSet += _)
      val newEntropy = featSet.foldLeft(0.0){ case (prob, value) =>
        val subDataSet = splitDataSet(dataSet, index, value)
        val p = subDataSet.rows.toDouble / dataSet.rows.toDouble
        prob + p * calcShannonEnt(subDataSet)
      }
      val infoGain = baseEntropy - newEntropy
      if (infoGain > bestInfoGain) {
        bestInfoGain = infoGain
        bestFeature = index
      }
    }
    bestFeature
  }

  def majorityCnt(classList: DenseVector[Double]) = {
    val labelCounts =  (0 until classList.activeSize).map { index =>
      classList(index)
    }.groupBy(l => l).map(t => (t._1, t._2.length))
    val entropy = labelCounts.foldLeft((0.0, 0)){ case (a, b) =>
        if(a._2 < b._2)
          b
        else
          a
    }
    entropy._1
  }

  def createTree(dataSet: DenseMatrix[Double], value: Double, labels: Seq[String]): Node = {
    val numFeatures = dataSet.cols - 1
    val classList = dataSet(::, numFeatures)
    val featSet = scala.collection.mutable.HashSet[Double]()
    classList.foreach(featSet += _)
    if(featSet.size == 1) {
      Node(classList(0).toString, value)
    }
    else {
      if(dataSet.cols == 1) {
        Node(majorityCnt(classList).toString, value)
      }
      else {
        val bestFeat = chooseBestFeatureToSplit(dataSet)
        val myTree = Node(labels(bestFeat), value)
        val featValues = dataSet(::, bestFeat)
        val featSet = scala.collection.mutable.HashSet[Double]()
        featValues.foreach(featSet += _)
        val remainderLabels = labels.zipWithIndex.filter(_._2 != bestFeat).map(_._1)
        featSet.foreach { v =>
          myTree.child += createTree(splitDataSet(dataSet, bestFeat, v), v, remainderLabels )
        }
        myTree
      }
    }
  }

  def testSplit(): Unit = {
    val dm = DenseMatrix(
      (1.0, 1.0, 1.0),
      (1.0, 1.0, 1.0),
      (1.0, 0.0, 0.0),
      (0.0, 1.0, 0.0),
      (0.0, 1.0, 0.0)
    )

    val test = createTree(dm, 0, Seq("No Surfacing", "flippers"))
    println(test)
//    println(calcShannonEnt(dm))
//
//    val test1 = splitDataSet(dm, 0, 1)
//    println(test1)
//
//    val test2 = splitDataSet(dm, 0, 0)
//    println(test2)
//
//    println(chooseBestFeatureToSplit(dm))
  }

  testSplit()


}
