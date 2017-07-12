package webscrapper

import scala.collection.mutable.ListBuffer
import java.time.LocalDateTime

object TestUtils {
  
  def makeRounds(game:GameSimulation, count:Int) = {
    for (i <- 1 to count) game.addRound(makeRound(game))
  }
  
  def makeRound(game:GameSimulation, roundType:RoundType = RoundType.CITIZEN) = new Round(game.players, game.start, roundType, 0, 0)
  
  def finishGameSimulation(game:GameSimulation) = {
    game.finish(Result.GOROD_WIN)
  }
  
  def makeGameSimulation() = {
    val players = ListBuffer[Player]()
    players += makePlayer("Andrey", Role.KOMISSAR)
    players += makePlayer("Oleg")
    players += makePlayer("Igor")
    players += makePlayer("Pavel")
    players += makePlayer("Anna", Role.MAFIA)
    players += makePlayer("Vika", Role.MAFIA)
    players += makePlayer("Дима")
    new GameSimulation(1, Location.KRESTY, LocalDateTime.now, players.toList, List())
  }
  
  def makeOzhaGameSimulation() = {
    val players = ListBuffer[Player]()
    players += makePlayer("Andrey", Role.KOMISSAR)
    players += makePlayer("Oleg")
    players += makePlayer("Igor")
    players += makePlayer("Pavel")
    players += makePlayer("Anna", Role.MAFIA)
    players += makePlayer("Vika", Role.MAFIA)
    players += makePlayer("Дима")
    players += makePlayer("Sergey", Role.BOSS)
    players += makePlayer("Vika2", Role.DOCTOR)
    players += makePlayer("Vika3", Role.MANIAC)
    players += makePlayer("Vika4", Role.SERZHANT)
    players += makePlayer("Vika5", Role.CHILD_GIRL)
    players += makePlayer("Vika6")
    new GameSimulation(1, Location.OZHA, LocalDateTime.now, players.toList, List())
  }
  
  def makePlayer(name:String, role:Role = Role.CITIZEN) = {
    new Player(role, FinishStatus.ALIVE, name)
  }
  
  def makeFinishedGame(result:Result, status:OmonStatus = null) = {
    val game = makeGameSimulation()
    game.finish(result)
    /*if(status != null) game.omonHappened(status)
    game.calculateTournamentResult*/
    game.toGame
  }
  
  def makePlainRound(roundType:RoundType = RoundType.INITIAL, roles:List[Role] = List(Role.CITIZEN)) = {
    val players = roles.indices.zip(roles).map(pair => makePlayer("Andrey" + pair._1, pair._2)).toList
    new Round(players, LocalDateTime.now, roundType, 0, 0)
  }
  
  def makeUser(name:String) = {
    User(name)
  }
}