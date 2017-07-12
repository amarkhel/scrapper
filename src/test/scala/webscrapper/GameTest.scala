/*package webscrapper

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.collection.mutable.ListBuffer
import webscrapper.TestUtils._
import java.time.LocalDateTime
import webscrapper.TournamentResult.OMON_1
import webscrapper.OmonStatus.ONE

@RunWith(classOf[JUnitRunner])
class GameTest extends FunSuite with Matchers {
  
  test("game should return correct count of rounds when game just started") {
    val game = makeGame()
    game.countRounds should be (1)
  }
  
  test("game should return correct count of rounds after some rounds completed") {
    val game = makeGame()
    makeRounds(game, 3)
    game.countRounds should be (4)
  }
  
  test("game should throw exception when it is not completed and someone asked to create statistics") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.calculateStatistics
    }
  }
  
  test("game should properly calculate statistics") {
    val game = makeGame()
    makeRounds(game, 3)
    Thread.sleep(100)
    finishGame(game)
    game.calculateStatistics
    game.statistics.players.size should be (7)
  }
  
  test("game should throw exception when someone tried to access to round that not exist") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.round(-1)
    }
    intercept[IllegalArgumentException]{
      game.round(24)
    }
  }
  
  test("game should return correct round") {
    val game = makeGame()
    makeRounds(game, 2)
    val first = game.round(0)
    first.typeIs(RoundType.INITIAL) should be (true)
    first.order should be (0)
    val second = game.round(1)
    second.typeIs(RoundType.CITIZEN) should be (true)
    second.order should be (1)
  }
  
  test("game should correctly add rounds") {
    val game = makeGame()
    game.countRounds should be (1)
    game.addRound(makeRound(game))
    game.countRounds should be (2)
  }
  
  test("game should throw exception when round is null") {
    val game = makeGame()
    game.countRounds should be (1)
    intercept[IllegalArgumentException]{
      game.addRound(null)
    }
  }
  
  test("game should find correct player") {
    val game = makeGame()
    val player = game.findPlayer("Andrey")
    player.name should be ("Andrey")
    val player2 = game.findPlayer("Дима")
    player2.name should be ("Дима")
    intercept[IllegalArgumentException]{
      game.findPlayer(null)
    }
    intercept[IllegalArgumentException]{
      game.findPlayer("")
    }
    intercept[IllegalArgumentException]{
      game.findPlayer("12345")
    }
  }
  
  test("game should find correct player by role") {
    val game = makeGame()
    val citizens = game.findPlayersByRole(Role.CITIZEN)
    citizens.size should be (4)
    val mafia = game.findPlayersByRole(Role.MAFIA)
    mafia.size should be (2)
    val komissar = game.findPlayersByRole(Role.KOMISSAR)
    komissar.size should be (1)
    intercept[IllegalArgumentException]{
      game.findPlayersByRole(null)
    }
    intercept[IllegalArgumentException]{
      game.findPlayersByRole(Role.BOSS)
    }
  }
  
  test("starteAt method should be correct") {
    val game = makeGame()
    val started = game.startedAt
    started shouldNot be (null)
    started should include (" по московскому времени")
  }
  
  test("finish method should be correct") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.finishedAt 
    }
    game.result should be (null)
    game.finish(Result.GOROD_WIN)
    game.finishedAt shouldNot be (null)
    game.result should be (Result.GOROD_WIN)
    intercept[IllegalArgumentException]{
      game.finish(Result.GOROD_WIN) 
    }
    val game2 = makeGame()
    intercept[IllegalArgumentException]{
      game.finish(null)
    }
  }
  
  test("finishedAt method should be correct") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.finishedAt 
    }
    game.finish(Result.GOROD_WIN)
    game.finishedAt shouldNot be (null)
    game.finishedAt should include (" по московскому времени")
  }
  
  test("duration method should be correct") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.finishedAt 
    }
    game.finish(Result.GOROD_WIN)
    game.finishedAt shouldNot be (null)
    game.finishedAt should include (" по московскому времени")
  }
  
  test("wasOmon method should be correct") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.wasOmon
    }
    game.finish(Result.GOROD_WIN)
    game.wasOmon should be (false)
    val game2 = makeGame()
    game2.finish(Result.GOROD_WIN)
    game2.omonHappened(OmonStatus.ONE)
    game2.wasOmon should be (true)
  }
  
  test("omonHappened method should be correct") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.omonHappened(null)
    }
    game.finish(Result.GOROD_WIN)
    game.omonHappened(ONE)
    game.wasOmon should be (true)
  }
  
  test("omonStatus method should be correct") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.omonHappened(null)
    }
    game.finish(Result.GOROD_WIN)
    game.omonHappened(ONE)
    game.wasOmon should be (true)
  }
  
  test("timeout method should be correct") ({
    val game = makeGame()
    makeRound(game)
    val notExist = new Player(Role.CITIZEN, FinishStatus.ALIVE, "Sergey")
    intercept[IllegalArgumentException]{
      game.timeout(notExist)
    }
    game.state.alivePlayers.size should be (7)
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    game.currentRound.hasTimeout should be (false)
    game.timeout(komissar)
    game.currentRound.hasTimeout should be (true)
    val player = new Player(Role.CITIZEN, FinishStatus.ALIVE, "Дима")
    game.timeout(player)
    game.currentRound.timeouted.size should be (2)
  })
  
  test("hasTimeout method should be correct") {
    val game = makeGame()
    game.hasTimeout should be (false)
    makeRound(game)
    game.hasTimeout should be (false)
    val player = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    game.timeout(player)
    game.hasTimeout should be (true)
  }
  
  test("alive method should be correct") {
    val game = makeGame()
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    val player = new Player(Role.CITIZEN, FinishStatus.ALIVE, "Дима")
    val notExist = new Player(Role.CITIZEN, FinishStatus.ALIVE, "Димаfgdf")
    intercept[IllegalArgumentException]{
      game.alive(notExist)
    }
    intercept[IllegalArgumentException]{
      game.alive(null)
    }
    game.alive(komissar) should be (true)
    game.alive(player) should be (true)
    game.timeout(komissar)
    game.alive(komissar) should be (false)
    game.alive(player) should be (true)
  }
  
  test("resetFrozen method should be correct") {
    val game = makeOzhaGame()
    game.addRound(makeRound(game, RoundType.BOSS))
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    val notExist = new Player(Role.CITIZEN, FinishStatus.ALIVE, "Димаfgdf")
    val player = new Player(Role.MAFIA, FinishStatus.ALIVE, "Дима")
    intercept[IllegalArgumentException]{
      game.resetFrozen(notExist)
    }
    intercept[IllegalArgumentException]{
      game.resetFrozen(player)
    }
    intercept[IllegalArgumentException]{
      game.resetFrozen(null)
    }
    game.currentRound.hasMoroz should be (false)
    game.resetFrozen(komissar)
    game.currentRound.hasMoroz should be (true)
  }
  
  test("Kill method should be correct") {
    val game = makeGame()
    val player = new Player(Role.MAFIA, FinishStatus.ALIVE, "Anna")
    val player2 = new Player(Role.MAFIA, FinishStatus.ALIVE, "Vika")
    val komissar = new Player(Role.CITIZEN, FinishStatus.ALIVE, "Andrey")
    val notExist = new Player(Role.MAFIA, FinishStatus.ALIVE, "Not exist")
    game.state.alivePlayers.size should be (7)
    intercept[IllegalArgumentException]{
      game.kill(notExist)
    }
    intercept[IllegalArgumentException]{
      game.kill(null)
    }
    game.currentRound.killedByMafia should be (null)
    game.currentRound.killedByManiac should be (null)
    game.kill(komissar)
    game.currentRound.killedByMafia shouldNot be (null)
    game.currentRound.killedByManiac should be (null)
    game.currentRound.killedByMafia.name should be ("Andrey")
    game.state.alivePlayers.size should be (6)
  }
  
  test("Go prison method should be correct") {
    val game = makeGame()
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    val notExist = new Player(Role.MAFIA, FinishStatus.ALIVE, "Not exist")
    game.state.alivePlayers.size should be (7)
    intercept[IllegalArgumentException]{
      game.goPrizon(notExist)
    }
    intercept[IllegalArgumentException]{
      game.goPrizon(null)
    }
    game.currentRound.hasPrisoners should be (false)
    game.addRound(makeRound(game, RoundType.CITIZEN))
    game.goPrizon(komissar)
    game.currentRound.hasPrisoners should be (true)
    game.state.alivePlayers.size should be (6)
  }
  
  test("check method should be correct") {
    val game = makeGame()
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    val player = new Player(Role.CITIZEN, FinishStatus.ALIVE, "Дима")
    val notExist = new Player(Role.MAFIA, FinishStatus.ALIVE, "Not exist")
    game.state.alivePlayers.size should be (7)
    intercept[IllegalArgumentException]{
      game.check(notExist, player)
    }
    intercept[IllegalArgumentException]{
      game.check(null, null)
    }
    intercept[IllegalArgumentException]{
      game.check(null, player)
    }
    intercept[IllegalArgumentException]{
      game.check(player, null)
    }
    intercept[IllegalArgumentException]{
      game.check(player, player)
    }
    intercept[IllegalArgumentException]{
      game.check(player, komissar)
    }
    game.addRound(makeRound(game, RoundType.KOMISSAR))
    game.currentRound.hasChecks should be (false)
    game.check(komissar, player)
    game.currentRound.hasChecks should be (true)
    game.state.alivePlayers.size should be (7)
  }
  
  test("moroz method should be correct") {
    val game = makeOzhaGame()
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    val boss = new Player(Role.BOSS, FinishStatus.ALIVE, "Sergey")
    val notExist = new Player(Role.MAFIA, FinishStatus.ALIVE, "Not exist")
    val mafia = new Player(Role.MAFIA, FinishStatus.ALIVE, "Not exist")
    game.state.alivePlayers.size should be (13)
    intercept[IllegalArgumentException]{
      game.froze(null, null)
    }
    intercept[IllegalArgumentException]{
      game.froze(null, komissar)
    }
    intercept[IllegalArgumentException]{
      game.froze(boss, null)
    }
    intercept[IllegalArgumentException]{
      game.froze(boss, boss)
    }
    intercept[IllegalArgumentException]{
      game.froze(boss, mafia)
    }
    intercept[IllegalArgumentException]{
      game.froze(komissar, boss)
    }
    game.addRound(makeRound(game, RoundType.BOSS))
    game.currentRound.hasMoroz should be (false)
    game.froze(boss, komissar)
    game.currentRound.hasMoroz should be (true)
    game.state.alivePlayers.size should be (13)
  }
  
  test("doctorAttempt method should be correct") {
    val game = makeOzhaGame()
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    val doctor = new Player(Role.DOCTOR, FinishStatus.ALIVE, "Vika2")
    val notExist = new Player(Role.MAFIA, FinishStatus.ALIVE, "Not exist")
    val mafia = new Player(Role.MAFIA, FinishStatus.ALIVE, "Not exist")
    game.state.alivePlayers.size should be (13)
    intercept[IllegalArgumentException]{
      game.doctorAttempt(null, null)
    }
    intercept[IllegalArgumentException]{
      game.doctorAttempt(null, komissar)
    }
    intercept[IllegalArgumentException]{
      game.doctorAttempt(doctor, null)
    }
    intercept[IllegalArgumentException]{
      game.doctorAttempt(doctor, doctor)
    }
    intercept[IllegalArgumentException]{
      game.doctorAttempt(doctor, mafia)
    }
    intercept[IllegalArgumentException]{
      game.doctorAttempt(komissar, doctor)
    }
    game.addRound(makeRound(game, RoundType.MAFIA))
    game.currentRound.hasDoctorAttempt should be (false)
    game.doctorAttempt(doctor, komissar)
    game.currentRound.hasDoctorAttempt should be (true)
    game.state.alivePlayers.size should be (13)
  }
  
  test("incrementRoundOrder method should be correct") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.incrementRoundOrder(null)
    }
    val round = new Round(game.state.alivePlayers.toList, game.start, RoundType.BOSS)
    round.order should be (0)
    game.incrementRoundOrder(round)
    round.order should be (1)
  }
  
  test("current round should be returned") {
    val game = makeGame()
    game.currentRound shouldNot be (null)
    game.currentRound.order should be (0)
    makeRounds(game, 3)
    game.currentRound.order should be (3)
  }
  
  test("calculate tournament results should work") {
    val game = makeGame()
    intercept[IllegalArgumentException]{
      game.calculateTournamentResult
    }
    game.finish(Result.GOROD_WIN)
    game.calculateTournamentResult
    game._tournamentResult should be (TournamentResult.GOROD_WIN)
    game.omonHappened(OmonStatus.ONE)
    game.calculateTournamentResult
    game._tournamentResult should be (TournamentResult.OMON_1)
  }
  
  test("get winner points should work") {
    val game = makeGame()
    val komissar = new Player(Role.KOMISSAR, FinishStatus.ALIVE, "Andrey")
    val boss = new Player(Role.BOSS, FinishStatus.ALIVE, "Sergey")
    game.finish(Result.GOROD_WIN)
    game.calculateTournamentResult
    game.getWinPoints(komissar) should be (1.0)
    game.getWinPoints(boss) should be (0.0)
    val game2 = makeGame()
    game2.finish(Result.MAFIA_WIN)
    game2.calculateTournamentResult
    game2.getWinPoints(komissar) should be (0.0)
    game2.getWinPoints(boss) should be (1.0)
    val game3 = makeGame()
    game3.finish(Result.GOROD_WIN)
    game3.omonHappened(OmonStatus.ONE)
    game3.calculateTournamentResult
    game3.getWinPoints(komissar) should be (0.75)
    game3.getWinPoints(boss) should be (0.25)
    val game4 = makeGame()
    game4.finish(Result.GOROD_WIN)
    game4.omonHappened(OmonStatus.TWO)
    game4.calculateTournamentResult
    game4.getWinPoints(komissar) should be (0.5)
    game4.getWinPoints(boss) should be (0.5)
    val game5 = makeGame()
    game5.finish(Result.GOROD_WIN)
    game5.omonHappened(OmonStatus.THREE)
    game5.calculateTournamentResult
    game5.getWinPoints(komissar) should be (0.25)
    game5.getWinPoints(boss) should be (0.75)
  }
}*/