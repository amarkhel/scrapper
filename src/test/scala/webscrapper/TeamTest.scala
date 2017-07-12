package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import TestUtils._
import scala.collection.mutable.ListBuffer

@RunWith(classOf[JUnitRunner])
class TeamTest extends FunSuite with Matchers {
  
  test("containsPlayer should return correct results") {
    val players = ListBuffer[User]()
    players += makeUser("Andrey")
    players += makeUser("Vika")
    val team = new Team("U", players.toList)
    team.containsPlayer("Andrey") should be (true)
    team.containsPlayer("Vika") should be (true)
    team.containsPlayer("Andrey2") should be (false)
    team.containsPlayer("") should be (false)
    intercept[IllegalArgumentException]{
      team.containsPlayer(null)
    }
    val team2 = new Team("U", List())
    team2.containsPlayer("Andrey") should be (false)
    team2.containsPlayer("") should be (false)
  }
}