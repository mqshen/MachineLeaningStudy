package ch02

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by goldratio on 1/22/15.
 */
object AmProperty extends App {
  val conf = new SparkConf().setAppName("ss").setMaster("local")
  val sc = new SparkContext(conf)

  val file = sc.textFile("/Users/goldratio/WorkspaceGroup/DataWorkspace/study/data/1.csv")
  val counts = file.map(line => line.split(",")).filter { t => println(t(36)); t(36) == 24 }.count() //.map(word => (word, 1)).reduceByKey(_ + _)
  println(counts)

}
