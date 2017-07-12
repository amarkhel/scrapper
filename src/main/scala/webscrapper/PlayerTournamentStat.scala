package webscrapper

import scala.collection.mutable.ListBuffer
import webscrapper.util.StatUtils
import webscrapper.util.StatUtils._
import scala.collection.mutable.HashMap

//TODO rework to immutable check that parameters > 0
class PlayerTournamentStat(val name:String, private var _countPlayed:Double = 0, private var _points:Double = 0.0, 
    private var _possiblePoints:Double = 0, private var _supoints:Double = 0, private var _bestinsu:Int = 0, private var _winPoints:Double = 0,
    private var _messages:Double = 0, private var _smiles:Double = 0) {

  type Map[T, E] = scala.collection.mutable.HashMap[T, E]
  
  val finishStatuses = new collection.mutable.HashMap[FinishStatus, Int]
  val roles = new Map[Role, Int]
  var prisoned = 0
  var killed = 0
  var checked = 0
  var prisonedFirst = 0
  var killedFirst = 0
  var checkedFirst = 0
  require(name != null && !name.isEmpty)
  def countPlayed = _countPlayed
  def countPlayed(predicate:Player => Boolean) = games.filter{_.statistics.playerInRole(name, predicate)}.size
  def points = _points
  def possiblePoints = _possiblePoints
  def supoints = _supoints
  def bestinsu = _bestinsu
  def winPoints = _winPoints
  def messages = _messages
  def smiles = _smiles
  val rolesDistribution = collection.mutable.HashMap[Role, Int]()
  val games = ListBuffer[Game]()
  val messagesRound = ListBuffer[Double]()
  val messagesMinute = ListBuffer[Double]()
  
  def update(points:Double, possiblePoints:Double, supoints:Double, winPoints:Double, best:Boolean, messages:Double, smiles:Double, role:Role, game:Game, status:FinishStatus) = {
    val roundStats = game.statistics.roundStats
    val messageStats = game.statistics.messageStats
    _countPlayed += 1
    _points += points
    _possiblePoints += possiblePoints
    _supoints += supoints
    _winPoints += winPoints
    if(best) _bestinsu += 1
    _messages += messages
    _smiles += smiles
    val count = rolesDistribution.getOrElse(role, 0)
    rolesDistribution.update(role, count+1)
    games += game
    increment(finishStatuses, status)
    increment(roles, role)
    val player = game.findPlayer(name).get
    messagesRound += messageStats.avgMessagesRoundForPlayer(player)
    messagesMinute += messageStats.messagesPerMinute(player)
    if(roundStats.killed(player)) killed = killed+1
    if(roundStats.checked(player)) checked = checked +1
    if(roundStats.prisoned(player)) prisoned = prisoned +1
    if(roundStats.killedFirst(player)) killedFirst = killedFirst + 1
    if(roundStats.checkedFirst(player)) checkedFirst = checkedFirst + 1
    if(roundStats.prisonedFirst(player)) prisonedFirst = prisonedFirst + 1
  }
  
  def percentWinInRole(rolePredicate:Option[Player] => Boolean) = {
    val winPoints = games.map(game =>  {
      val player = game.findPlayer(name)
      if (rolePredicate(player)) game.getWinPoints(player) else -1
    }).filter(_ > 0)
    roundDouble(winPoints.sum/winPoints.size)
  }
  
  private def update[A](map: Map[A, Double], key: A, value: Double) = {
    val addon = map.getOrElse(key, 0.0)
    map.update(key, roundDouble(addon + value))
  }
  
  private def increment[A](map: Map[A, Int], value: A) = {
    val count = map.getOrElse(value, 0) + 1
    map += (value -> count)
  }
  
  def firstVoteToMafiaPercent = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    val roundsWithVotes = games.flatMap(_.statistics.citizenRoundsBeforePrisonedVotes(player))
    val countMafiaVotes = roundsWithVotes.filter(_.hasVoteToMafiaFrom(player)).size
    StatUtils.roundDouble(countMafiaVotes.toDouble / roundsWithVotes.size)
  }
  
  def countAlibi = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    val mafiagames = games.filter{_.statistics.playerInRole(name, _.isMafia)}
    mafiagames.map(_.statistics.voteStats.countCitizenVotesToMafia(player)).sum
  }
  
  def countChecked = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    games.filter{!_.statistics.playerInRole(name, _.isKomissar)}.filter(_.statistics.wasChecked(player)).size
    
  }
  
  def countCheckedPercent = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    val g = games.filter{!_.statistics.playerInRole(name, _.isKomissar)}
    val g2 = g.filter(_.statistics.wasChecked(player)).size
    g2.toDouble / g.size
  }
  
  def countPrisoned = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    games.filter(_.statistics.wasPrisoned(player)).size
  }
  
  def countPrisonedPercent = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    val g2 = games.filter(_.statistics.wasPrisoned(player)).size
    g2.toDouble / games.size
  }
  
  def countKilled = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    games.filter{!_.statistics.playerInRole(name, _.isMafia)}.filter(_.statistics.wasKilled(player)).size
  }
  
  def countKilledPercent = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    val g = games.filter{!_.statistics.playerInRole(name, _.isMafia)}
    val g2 = g.filter(_.statistics.wasKilled(player)).size
    g2.toDouble / g.size
  }
  
  def countAlibiPercent = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    val mafiagames = games.filter{_.statistics.playerInRole(name, _.isMafia)}
    val mafiaVotes = mafiagames.map(_.statistics.voteStats.countCitizenVotesToMafia(player)).sum
    val overall = mafiagames.map(_.statistics.voteStats.countCitizenVotesFrom(player)).sum
    if(overall > 0) StatUtils.roundDouble(mafiaVotes.toDouble/overall)*100 else 0
  }
  
  def votesToMafiaPercent = {
    val player = new Player(Role.BOSS, FinishStatus.ALIVE, name)
    val roundsWithVotes = games.flatMap(_.statistics.citizenRoundsWithVotes(player))
    val countMafiaVotes = roundsWithVotes.filter(_.hasVoteToMafiaFrom(player)).size
    StatUtils.roundDouble(countMafiaVotes.toDouble / roundsWithVotes.size)  
  }
  
  def messagesPerGame: Double = {
    require(messages >= 0)
    calculate(messages / countPlayed)
  }

  private def percentWins(rolePredicate:Player => Boolean, pointsPredicate: Option[Player] => Double) = {
    val citgames = games.filter{_.statistics.playerInRole(name, rolePredicate)}
    val points = citgames.map(game => pointsPredicate(game.findPlayer(name))).sum
    val possiblePoints = citgames.map(game => PointRule.findMaximumPossiblePoints(game, name)).sum
    if(points > 0 && citgames.size > 3)points.toDouble / possiblePoints else 0
  }
  
  def citizenWinPercent = percentWins(_.isCitizen, _.map(_.points).getOrElse(0.0))
  
  def komissarWinPercent = percentWins(_.isKomissar, _.map(_.points).getOrElse(0.0))
  
  def mafiaWinPercent = percentWins(_.isMafia, _.map(_.points).getOrElse(0.0))
  
  def citizenAverageSU = percentWins(_.isCitizen, _.map(_.achievementScore).getOrElse(0.0))
  
  def mafiaAverageSU = percentWins(_.isMafia, _.map(_.achievementScore).getOrElse(0.0))
  
  def komissarAverageSU = percentWins(_.isKomissar, _.map(_.achievementScore).getOrElse(0.0))
  
  def kpd: Double = {
    require(possiblePoints > 0 && points <= possiblePoints)
    calculate(100 * points / possiblePoints)
  }

  def kpdsu: Double = {
    //require(supoints >= 0)
    calculate(supoints / countPlayed)
  }

  def pointsPerGame: Double = {
    //require(points >= 0)
    calculate(points / countPlayed)
  }

  def percentWin: Double = {
    require(winPoints >= 0 && winPoints <= countPlayed)
    calculate(100 * winPoints / countPlayed)
  }

  private def calculate(op: => Double): Double = if (countPlayed <= 0) 0.0 else op
  
  override def toString = {
    s"$name : $points очков, $countPlayed игр, $possiblePoints возможных, $supoints по Ювентусу, был лучшим $bestinsu играх, $winPoints игровых, $messages сообщений, $smiles смайлов"
  }
}