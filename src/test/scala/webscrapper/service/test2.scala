package webscrapper.service

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.jsoup.nodes.Document
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import samples.DBTest
import scala.io.Source

class test2 extends App {
    //println(Source.fromFile("C:\\Users\\amarkhel\\Documents\\d.csv", "UTF-8").getLines.mkString(","))
    val s = new GameService
    val g = s.loadGame("3627341").get
    println(g.statistics.countManiacKills)
    
}