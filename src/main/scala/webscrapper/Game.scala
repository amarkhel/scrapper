package webscrapper

import java.time.LocalDateTime

import scala.collection.mutable.ListBuffer
import webscrapper.stats.GameStatistics
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import webscrapper.util.TimeUtils
import RoundType._
import webscrapper.util.StatUtils._
import Role._
import TournamentResult._
import OmonStatus._
import webscrapper.rules.RuleEngine
import webscrapper.stats.PlayerStatistics
import webscrapper.util.Logger

@SerialVersionUID(100L)
class Game(val players: List[Player], val id: Long, val location: Location, val start: LocalDateTime,
    finish: LocalDateTime, val rounds: List[Round],
    val result: Result, omon: Option[OmonStatus]) extends Serializable with Logger {
  protected def init = {
    require(location != null && start != null && id > 0 && players != null && players.size > 0 && finish != null && result != null && rounds != null && rounds.size > 0)
  }
  

  val tournamentResult = calculateTournamentResult

  lazy val statistics:GameStatistics = calculateStatistics
lazy val playerStats = calculatePointsStat
  def isGorodWin = tournamentResult == TournamentResult.GOROD_WIN
  def isMafiaWin = tournamentResult == TournamentResult.MAFIA_WIN
  def singleOmon = tournamentResult == TournamentResult.OMON_1
  def doubleOmon = tournamentResult == TournamentResult.OMON_2
  def tripleOmon = tournamentResult == TournamentResult.OMON_3
  def fourOmon = tournamentResult == TournamentResult.OMON_4
  /**
   *
   * @return count of rounds of the game
   */
  def countRounds = rounds size

  def countPlayers = players size

  def omonNominal = omon.map(_.countMafia).getOrElse(0)
  
  def wasOmon = omon.isDefined

  /**
   *  Method called to generate statistics and cache it in a local field
   */
  private def calculateStatistics = {
    log(s"Calculate statistics for $id");
    new GameStatistics(rounds.toList, players, start, finish)
  }
  
  private def calculatePointsStat = {
    log(s"Calculate points for $id");
    pointsStrategy.calculate(this)
    log(s"Calculate points calculated $id");
    new PlayerStatistics(players)
  }

  def bestPlayer = playerStats.best
  
  /**
   * @return string representation of moment, when game is started
   */
  def startedAt = TimeUtils.format(start)
  
  def findPlayer(name: String) = {
    require(name != null)
    players.find(_.name.trim == name)
  }

  /**
   * @return string representation of moment, when game is finished
   */
  def finishedAt = TimeUtils.format(finish)

  /**
   * @return string representation of game duration
   */
  def duration = TimeUtils.formatDuration(start, finish)

  private def pointsStrategy = new PointsCalcStrategy with RuleEngine

  private def calculateTournamentResult = omon.isDefined match {
    case true => omon.get match {
      case ONE => OMON_1
      case TWO => OMON_2
      case THREE => OMON_3
      case FOUR => OMON_4
    }
    case false => result match {
      case Result.MAFIA_WIN => MAFIA_WIN
      case Result.GOROD_WIN => GOROD_WIN
    }
  }

  def getWinPoints(player: Option[Player])(implicit tournamentName:TournamentName) = {
    require(player.isDefined && players.contains(player.get))
    tournamentName.name match {
      case "Межклан 2016" => player.get.isPositive match {
        case true => tournamentResult match {
          case GOROD_WIN => 1.0
          case MAFIA_WIN => 0.0
          case OMON_1 => 0.66
          case OMON_2 => 0.33
          case _ => 0.0
        }
        case false => tournamentResult match {
          case GOROD_WIN => 0.0
          case MAFIA_WIN => 1.0
          case OMON_1 => 0.33
          case OMON_2 => 0.66
          case _ => 0.0
        }
      }
      case _ => player.get.isPositive match {
        case true => tournamentResult match {
          case GOROD_WIN => 1.0
          case MAFIA_WIN => 0.0
          case OMON_1 => 0.75
          case OMON_2 => 0.5
          case OMON_3 => 0.25
          case _ => 0.0
        }
        case false => tournamentResult match {
          case GOROD_WIN => 0.0
          case MAFIA_WIN => 1.0
          case OMON_1 => 0.25
          case OMON_2 => 0.5
          case OMON_3 => 0.75
          case _ => 0.0
        }
      }
    }

  }
}

object InvalidGame extends Game(List(new Player(Role.BOSS, FinishStatus.ALIVE, "Dummy")), 1, Location.KRESTY, LocalDateTime.now,LocalDateTime.now, List(new Round(List(new Player(Role.BOSS, FinishStatus.ALIVE, "Dummy")), LocalDateTime.now, RoundType.INITIAL, 1, 1)), Result.GOROD_WIN, None)