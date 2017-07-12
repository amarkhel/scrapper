package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.collection.mutable.ListBuffer
import TestUtils._

@RunWith(classOf[JUnitRunner])
class PlayerStatisticQueryResultTest extends FunSuite with Matchers {
 
  test("toString should be correct"){
    val pl = ListBuffer[Player]()
    pl += makePlayer("Andrey")
    pl += makePlayer("Sergey")
    pl += makePlayer("Dima")
    val stat = new PlayerStatisticQueryResult(pl.toList, 2)
    stat.toString should be ("[Andrey(2),Sergey(2),Dima(2)]")
    intercept[IllegalArgumentException]{
      val stat = new PlayerStatisticQueryResult(List(), 2)
    }
  }
}