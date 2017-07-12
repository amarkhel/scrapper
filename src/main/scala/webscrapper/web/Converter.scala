package webscrapper.web

import scala.collection.convert.WrapAsJava
import scala.collection.convert.WrapAsScala
import scala.collection.JavaConversions

class Converter {
  def wrap(seq:Iterable[_]) = {
    WrapAsJava.asJavaCollection(seq)
  }
  
  def map(map:Map[_, _]) = {
    val ret = JavaConversions.mapAsJavaMap(map)
    ret
  }
}