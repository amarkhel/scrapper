/*package webscrapper.util

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import webscrapper.Player
import webscrapper.FinishStatus
import webscrapper.Role
import webscrapper.TestUtils._

@RunWith(classOf[JUnitRunner])
class StatUtilsTest extends FunSuite with Matchers {
  
  test("round doubles should work correctly") {
    StatUtils.roundDouble(0.11) should be (0.11)
    StatUtils.roundDouble(-0.11) should be (-0.11)
    StatUtils.roundDouble(0) should be (0)
    StatUtils.roundDouble(0.4949) should be (0.49)
    StatUtils.roundDouble(0.495) should be (0.5)
  }
  
  test("maximum should return correct values") {
    val players = Map(makePlayer("Test1") -> 1, makePlayer("Test2") -> 2)
    val max = StatUtils.max(players)
    max.count should be (2)
    max.players.size should be (1)
    max.players(0).name should be ("Test2")
  }
  
  test("maximum for empty collection should throw exception") {
    val players = Map[Player, Int]()
    intercept[IllegalArgumentException] {
      val max = StatUtils.max(players)
    }
  }
  
  test("maximum for large collection should return correct results") {
    val players = Map(makePlayer("Test1") -> 3, makePlayer("Test2") -> 2, makePlayer("Test3") -> 41, makePlayer("Test4") -> 1, makePlayer("Test5") -> 3)
    val max = StatUtils.max(players)
    max.count should be (41)
    max.players.size should be (1)
    max.players(0).name should be ("Test3")
  }
  
  test("maximum should return list of players if it all have maximum") {
    val players = Map(makePlayer("Test1") -> 3, makePlayer("Test2") -> 41, makePlayer("Test3") -> 41, makePlayer("Test4") -> 1, makePlayer("Test5") -> 3)
    val max = StatUtils.max(players)
    max.count should be (41)
    max.players.size should be (2)
    max.players.map(_.name).mkString(",") should include ("Test2")
    max.players.map(_.name).mkString(",") should include ("Test3")
  }
  
  test("minimum should return correct values") {
    val players = Map(makePlayer("Test1") -> 1, makePlayer("Test2") -> 2)
    val min = StatUtils.min(players)
    min.count should be (1)
    min.players.size should be (1)
    min.players(0).name should be ("Test1")
  }
  
  test("minimum for empty collection should throw exception") {
    val players = Map[Player, Int]()
    intercept[IllegalArgumentException] {
      val min = StatUtils.min(players)
    }
  }
  
  test("minimum for large collection should return correct results") {
    val players = Map(makePlayer("Test1") -> 3, makePlayer("Test2") -> 2, makePlayer("Test3") -> 41, makePlayer("Test4") -> 1, makePlayer("Test5") -> 3)
    val min = StatUtils.min(players)
    min.count should be (1)
    min.players.size should be (1)
    min.players(0).name should be ("Test4")
  }
  
  test("minimum should return list of players if it all have maximum") {
    val players = Map(makePlayer("Test1") -> 1, makePlayer("Test2") -> 41, makePlayer("Test3") -> 41, makePlayer("Test4") -> 1, makePlayer("Test5") -> 3)
    val min = StatUtils.min(players)
    min.count should be (1)
    min.players.size should be (2)
    min.players.map(_.name).mkString(",") should include ("Test1")
    min.players.map(_.name).mkString(",") should include ("Test4")
  }
}
*/