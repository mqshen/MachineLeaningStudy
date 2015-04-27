package ch04

import breeze.linalg.{Transpose, DenseVector, DenseMatrix}

/**
 * Created by goldratio on 1/28/15.
 */
object Bayes extends App {

  def loadDataSet() = {
    val postingList = Seq(
      Seq("my", "dog", "has", "flea", "problems", "help", "please"),
      Seq("maybe", "not", "take", "him", "to", "dog", "park", "stupid"),
      Seq("my", "dalmation", "is", "so", "cute", "I", "love", "him"),
      Seq("stop", "posting", "stupid", "worthless", "garbage"),
      Seq("mr", "licks", "ate", "my", "steak", "how", "to", "stop", "him"),
      Seq("quit", "buying", "worthless", "dog", "food", "stupid")
    )
    val classVec = Seq(0, 1, 0, 1, 0, 1)
    (postingList, classVec)
  }

  def createVocabList(dataSet: Seq[Seq[String]]) = {
    val vocabSet = scala.collection.mutable.Set[String]()
    dataSet.map(_.foreach(vocabSet.add(_)))
    vocabSet.toSeq
  }

  def setOfWords2Vec(vocabList: Seq[String], inputSet: Seq[String]) = {
    val returnVec = DenseVector.zeros[Double](vocabList.size)
    inputSet.foreach { world =>
      val index = vocabList.indexOf(world)
      if (index > -1) {
        returnVec(index) = 1
      }
      else {
        println(s"the word: $world is not in my Vocabulary!")
      }
    }
    returnVec
  }

  def trainNB0(trainMatrix: DenseMatrix[Double], trainCategory: Seq[Int]) = {
    val numTrainDocs = trainMatrix.rows
    val numWords = trainMatrix.cols
    val pAbuseive = trainCategory.foldLeft(0)( _ + _).toDouble / numTrainDocs
    val p0Num = DenseVector.zeros[Double](numWords)
    val p1Num = DenseVector.zeros[Double](numWords)
    var p0Denom = 0.0
    var p1Denom = 0.0
    (0 until numTrainDocs).foreach { i =>
      if(trainCategory(i) == 1) {
        p1Num += trainMatrix(i, ::).inner
        trainMatrix(i, ::).inner.foreach { p =>
          p1Denom = p1Denom  + p
        }
      }
      else {
        p0Num += trainMatrix(i, ::).inner
        trainMatrix(i, ::).inner.foreach { p =>
          p0Denom = p0Denom  + p
        }
      }
    }
    val p1Vect = p1Num / p1Denom
    val p0Vect = p0Num / p0Denom
    (p0Vect, p1Vect, pAbuseive)
  }

  val (listOPosts, listClasses) = loadDataSet()
  val myVocabList = createVocabList(listOPosts)


  println(myVocabList)

  val trainMat = DenseMatrix.zeros[Double](listOPosts.size, myVocabList.size)//[DenseVector[Int]]()
  listOPosts.zipWithIndex.foreach { case (postInDoc, index) =>
    trainMat(index, ::) := Transpose(setOfWords2Vec(myVocabList, postInDoc))
  }

  val (p0V, p1V, pAb) = trainNB0(trainMat, listClasses)

  println(p0V)

  println(p1V)

  println(pAb)
}
