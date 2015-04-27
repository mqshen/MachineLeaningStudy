package spark

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by goldratio on 3/6/15.
 */
object WordCount extends App {

  System.setProperty("java.library.path", "/Users/goldratio/Downloads")
  val conf = new SparkConf()
    .setMaster("mesos://101.195.251.186:5050")
    .setAppName("My app")
    .set("spark.executor.uri", "hdfs://192.168.5.6:9000/spark-1.2.1-bin-1.0.4.tgz")
  .set("mesos.native.library", "/Users/goldratio/Downloads/libmesos.so")
  val sc = new SparkContext(conf)

  val textFile = sc.textFile("hdfs://192.168.5.6:9000/README.md")
  textFile.count()
}
