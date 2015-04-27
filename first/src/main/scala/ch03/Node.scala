package ch03

/**
 * Created by goldratio on 1/27/15.
 */
case class Node( var name: String, var value: Double ) {
  val child = scala.collection.mutable.ArrayBuffer[Node]()
}
