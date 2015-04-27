package util

import breeze.linalg._

/**
 * Created by goldratio on 4/27/15.
 */
object MatrixUtil {

  def sigmoid(matrix: DenseVector[Double]) = {
    val rows: Int = matrix.length
    val result = DenseVector.zeros[Double](rows)
    (0 until rows).map { i =>
        result(i) = MathUtil.sigmoid(matrix(i))
    }
    result
  }

  def log(matrix: DenseVector[Double]) = {
    val rows = matrix.length
    val result = DenseVector.zeros[Double](rows)
    (0 until rows).map { i =>
        val log = Math.log(matrix(i))
        if (log.isInfinite && log < 0) {
          result(i) = 0
        }
        else {
          result(i) = log
        }
    }
    result
  }
}

object MathUtil {

  def sigmoid(z: Double): Double = {
    1.0 / (1.0 + Math.exp(-z))
  }

}