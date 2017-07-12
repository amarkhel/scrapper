/*package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import webscrapper.TestUtils._
import scala.collection.mutable.ListBuffer
import java.time.LocalDateTime
import webscrapper.Role.KOMISSAR

@RunWith(classOf[JUnitRunner])
class RoundTest extends FunSuite with Matchers {

  test("Round should instantiate correctly"){
    intercept[IllegalArgumentException]{
      new Round(null, null, null)
    }
    var players = ListBuffer[Player]()
    intercept[IllegalArgumentException]{
      new Round(players.toList, null, null)
    }
    val now = LocalDateTime.now
    intercept[IllegalArgumentException]{
      new Round(players.toList, now, null)
    }
    val roundType = RoundType.INITIAL
    intercept[IllegalArgumentException]{
      new Round(players.toList, now, roundType)
    }
    players += makePlayer("Andrey")
    val round = new Round(players.toList, now, roundType)
    round.countPlayers should be (1)
  }
  
  test("who should check method works correctly"){
    var round = makePlainRound()
    //round type not komissar
    intercept[IllegalArgumentException]{
      round.whoShouldCheck
    }
    round = makePlainRound(RoundType.CITIZEN, List(Role.KOMISSAR, Role.BOSS))
    //it is not a komissar round
    intercept[IllegalArgumentException]{
      round.whoShouldCheck
    }
    round = makePlainRound(RoundType.KOMISSAR, List(Role.CITIZEN, Role.BOSS))
    //there is no alive komissars
    intercept[IllegalArgumentException]{
      round.whoShouldCheck
    }
    round = makePlainRound(RoundType.KOMISSAR, List(Role.KOMISSAR, Role.BOSS, Role.SERZHANT))
    var player = round.whoShouldCheck
    player.role should be (Role.KOMISSAR)
    round = makePlainRound(RoundType.KOMISSAR, List(Role.BOSS, Role.SERZHANT))
    player = round.whoShouldCheck
    player.role should be (Role.SERZHANT)
  }
  
  test("killByMafia method should work correctly"){
    var round = makePlainRound(RoundType.KOMISSAR)
    intercept[IllegalArgumentException]{
      round.killByMafia(makePlayer("Test"))
    }
    round = makePlainRound(RoundType.KOMISSAR, List(Role.BOSS, Role.SERZHANT))
    var serzhant = round.alivePlayers(Role.SERZHANT)(0)
    var boss = round.alivePlayers(Role.BOSS)(0)
    intercept[IllegalArgumentException]{
      round.killByMafia(null)
    }
    intercept[IllegalArgumentException]{
      round.killByMafia(serzhant)
    }
    intercept[IllegalArgumentException]{
      round.killByMafia(makePlayer("Test"))
    }
    round = makePlainRound(RoundType.MAFIA, List(Role.BOSS, Role.SERZHANT, Role.CHILD, Role.CHILD_GIRL, Role.CITIZEN))
    serzhant = round.alivePlayers(Role.SERZHANT)(0)
    boss = round.alivePlayers(Role.BOSS)(0)
    var child = round.alivePlayers(Role.CHILD)(0)
    var childW = round.alivePlayers(Role.CHILD_GIRL)(0)
    intercept[IllegalArgumentException]{
      round.killByMafia(boss)
    }
    intercept[IllegalArgumentException]{
      round.killByMafia(child)
    }
    intercept[IllegalArgumentException]{
      round.killByMafia(childW)
    }
    round.killByMafia(serzhant)
    round.killedByMafia shouldNot be (null)
    round.killedByMafia.name should be ("Andrey1")
    round.killedByManiac should be (null)
    val citizen = round.alivePlayers(Role.CITIZEN)(0)
    intercept[IllegalArgumentException]{
      round.killByMafia(citizen)
    }
    round.killedByMafia shouldNot be (null)
    round.killedByMafia.name should be ("Andrey1")
    round.killedByManiac should be (null)
    round = makePlainRound(RoundType.MAFIA, List(Role.SERZHANT, Role.CHILD, Role.CHILD_GIRL))
    serzhant = round.alivePlayers(Role.SERZHANT)(0)
    intercept[IllegalArgumentException]{
      round.killByMafia(serzhant)
    }
  }
  
  test("killByManiac method should work correctly"){
    var round = makePlainRound(RoundType.KOMISSAR)
    intercept[IllegalArgumentException]{
      round.killByManiac(makePlayer("Test"))
    }
    round = makePlainRound(RoundType.KOMISSAR, List(Role.BOSS, Role.SERZHANT))
    var serzhant = round.alivePlayers(Role.SERZHANT)(0)
    var boss = round.alivePlayers(Role.BOSS)(0)
    intercept[IllegalArgumentException]{
      round.killByManiac(null)
    }
    intercept[IllegalArgumentException]{
      round.killByManiac(serzhant)
    }
    intercept[IllegalArgumentException]{
      round.killByManiac(makePlayer("Test"))
    }
    round = makePlainRound(RoundType.KOMISSAR, List(Role.BOSS, Role.SERZHANT, Role.MANIAC))
    serzhant = round.alivePlayers(Role.SERZHANT)(0)
    intercept[IllegalArgumentException]{
      round.killByManiac(serzhant)
    }
    round = makePlainRound(RoundType.MAFIA, List(Role.BOSS, Role.SERZHANT, Role.CHILD, Role.MANIAC))
    serzhant = round.alivePlayers(Role.SERZHANT)(0)
    boss = round.alivePlayers(Role.BOSS)(0)
    var child = round.alivePlayers(Role.CHILD)(0)
    var maniac = round.alivePlayers(Role.MANIAC)(0)
    intercept[IllegalArgumentException]{
      round.killByManiac(maniac)
    }
    intercept[IllegalArgumentException]{
      round.killByManiac(makePlayer("Test"))
    }
    round.killByManiac(serzhant)
    round.killedByManiac shouldNot be (null)
    round.killedByManiac.name should be ("Andrey1")
    round.killedByMafia should be (null)
    intercept[IllegalArgumentException]{
      round.killByManiac(child)
    }
    round.killedByManiac shouldNot be (null)
    round.killedByManiac.name should be ("Andrey1")
    round.killedByMafia should be (null)
    round = makePlainRound(RoundType.MAFIA, List(Role.SERZHANT, Role.CHILD, Role.CHILD_GIRL))
    serzhant = round.alivePlayers(Role.SERZHANT)(0)
    intercept[IllegalArgumentException]{
      round.killByMafia(serzhant)
    }
  }
  
  test("add message should behave correctly"){
    val round = makePlainRound(RoundType.KOMISSAR, List(Role.BOSS, Role.SERZHANT))
    val player = round.alivePlayers(Role.BOSS)(0)
    round.countMessages should be (0)
    round.addMessage(new Message(player, "hi", List()))
    round.countMessages should be (1)
    intercept[IllegalArgumentException]{
      round.addMessage(null)
    }
  }
  
  test("add vote should behave correctly"){
    val round = makePlainRound(RoundType.KOMISSAR, List(Role.BOSS, Role.SERZHANT))
    val komissar = round.alivePlayers(Role.SERZHANT)(0)
    val boss = round.alivePlayers(Role.BOSS)(0)
    round.countVotesFor(boss) should be (0)
    round.addVote(new Vote(komissar, boss))
    round.countVotesFor(boss) should be (1)
    round.countVotesFor(komissar) should be (0)
    intercept[IllegalArgumentException]{
      round.addVote(null)
    }
    intercept[IllegalArgumentException]{
      round.addVote(new Vote(null, boss))
    }
    intercept[IllegalArgumentException]{
      round.addVote(new Vote(boss, null))
    }
    intercept[IllegalArgumentException]{
      round.addVote(new Vote(boss, boss))
    }
    intercept[IllegalArgumentException]{
      round.addVote(new Vote(boss, makePlayer("Test")))
    }
    intercept[IllegalArgumentException]{
      round.addVote(new Vote(makePlayer("Test2"), makePlayer("Test")))
    }
    intercept[IllegalArgumentException]{
      round.addVote(new Vote(makePlayer("Test2"), boss))
    }
  }
}*/