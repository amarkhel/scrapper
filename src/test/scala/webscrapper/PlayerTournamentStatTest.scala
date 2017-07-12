/*package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PlayerTournamentStatTest extends FunSuite with Matchers {
  test(" for messagesPerGame method"){
    val stat = new PlayerTournamentStat("Statistics")
    stat.messagesPerGame should be (0.0)
    stat.messages = 300
    stat.countPlayed = 6
    stat.messagesPerGame should be (50.0)
    stat.messages = 333
    stat.messagesPerGame should be (55.5 +- 0.01)
    stat.messages = 0
    stat.messagesPerGame should be (0.0)
    intercept[IllegalArgumentException]{
      stat.messages =  -7
      stat.messagesPerGame
    }
  }
  
  test(" for kpd method"){
    val stat = new PlayerTournamentStat("Statistics")
    intercept[IllegalArgumentException]{
      stat.kpd
    }
    stat.points = 10
    stat.possiblePoints = 15
    stat.countPlayed = 6
    stat.kpd should be (66.66 +- 0.01)
    stat.points = 0
    stat.possiblePoints = 15
    stat.kpd should be (0.0)
    stat.points = 10
    stat.possiblePoints = 10
    stat.kpd should be (100.0)
    intercept[IllegalArgumentException]{
      stat.points = 10
      stat.possiblePoints = 9
      stat.kpd
    }
    intercept[IllegalArgumentException]{
      stat.points = -1
      stat.possiblePoints = 9
      stat.kpd
    }
    intercept[IllegalArgumentException]{
      stat.points = 0.5
      stat.possiblePoints = -2
      stat.kpd
    }
  }
  
  test(" for kpdsu method"){
    val stat = new PlayerTournamentStat("Statistics")
    stat.kpdsu should be (0.0)
    stat.supoints = 300
    stat.countPlayed = 6
    stat.kpdsu should be (50.0)
    stat.supoints = 333
    stat.kpdsu should be (55.5 +- 0.01)
    stat.supoints = 0
    stat.kpdsu should be (0.0)
    intercept[IllegalArgumentException]{
      stat.supoints =  -7
      stat.kpdsu
    }
  }
  
  test(" for pointsPerGame method"){
    val stat = new PlayerTournamentStat("Statistics")
    stat.pointsPerGame should be (0.0)
    stat.points = 300
    stat.countPlayed = 6
    stat.pointsPerGame should be (50.0)
    stat.points = 333
    stat.pointsPerGame should be (55.5 +- 0.01)
    stat.points = 0
    stat.pointsPerGame should be (0.0)
    intercept[IllegalArgumentException]{
      stat.points =  -7
      stat.pointsPerGame
    }
  }
  
  test(" for percentWin method"){
    val stat = new PlayerTournamentStat("Statistics")
    stat.percentWin should be (0.0)
    stat.countWin = 3
    stat.countPlayed = 6
    stat.percentWin should be (50.0)
    stat.countWin = 4
    stat.percentWin should be (66.66 +- 0.01)
    stat.countWin = 0
    stat.percentWin should be (0.0)
    intercept[IllegalArgumentException]{
      stat.countWin =  -7
      stat.percentWin
    }
    intercept[IllegalArgumentException]{
      stat.countWin =  7
      stat.percentWin
    }
  }
}*/