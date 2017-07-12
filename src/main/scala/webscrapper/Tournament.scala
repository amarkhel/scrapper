package webscrapper

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import webscrapper.util.StatUtils._
import java.nio.file.Paths
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import scala.collection.immutable.HashMap
import scala.collection.immutable.ListMap
import webscrapper.util.RussianStemmer
import webscrapper.stats.GameStatistics

class Tournament(val tournament: String, val teams: List[Team], val parts: List[Part], val penalties: List[Penalty]) {
  implicit val tournamentName = TournamentName(tournament)
  type Map[T, E] = mutable.HashMap[T, E]
  val games = findAllGames(parts)
  val mapRoles = new Map[Role, Map[String, Double]]
  val mapResults: collection.Map[TournamentResult, Int] = parts.map(_.games).flatten.groupBy { _.tournamentResult }.map { case (key, value) => (key, value.size) }
  val partitionPoints = teamList
  val partitionPointsWithPenalties = teamList
  val _overallPoints = teamMapDouble
  val _overallPointsWithPenalty = teamMapDouble
  val _overallPointsWithDrop = teamMapDouble
  val _overallPointsPossible = teamMapDouble
  val _overallPointsPercent = teamMapDouble
  val _overallsuPoints = teamMapDouble
  val instantStanding = teamMapListDouble
  val teamStat = new Map[String, Map[String, PlayerTournamentStat]]
  val teamStatPercent = new Map[String, Map[String, Double]]
  val teamStatFinish = new Map[String, Map[FinishStatus, Int]]
  val prisoned = teamMapInteger
  val killed = teamMapInteger
  val checked = teamMapInteger
  val prisonedFirst = teamMapInteger
  val killedFirst = teamMapInteger
  val checkedFirst = teamMapInteger
  val longest = parts.map(_.games).flatten.maxBy(_.duration)
  val shortest = parts.map(_.games).flatten.minBy(_.duration)
  val _messages = teamMapDouble
  val _smiles = teamMapDouble
  val _messagesRound = teamMapDouble
  val _messagesMinute = teamMapDouble
  val _messagesGame = teamMapDouble
  val smilesMap = new Map[String, Int]
  val countPlayed = new Map[String, Int]
  
  val pointRules = PointRule.get(tournament)

  calculate
  val playerStat = teamStat.flatMap(e => e._2).values.toList
  
  def smilePopularity = {
    smilesMap.map { case (k, v) => new SmileHolder(k, v) }.toList.sortBy(_.count).reverse.take(10).toList
  }

  def bestMessages = top10(_.messages)

  def bestSmiles = top10(_.smiles)

  def bestMessagesPerGame = top10(_.messagesPerGame)

  def bestCount = top10(_.countPlayed)

  def bestWin = top10(_.winPoints)

  def bestKPD = top10(_.kpd)

  def bestPoints = top10(_.points)

  def bestPointssu = top10(_.kpdsu)

  def bestsucount = top10(_.bestinsu)

  def bestperGame = top10(_.pointsPerGame)

  def bestPercent = top10(_.percentWin)
  
  def bestFirstVote = top10(_.firstVoteToMafiaPercent)
  
  def bestVoters = top10(_.votesToMafiaPercent)
  
  def bestCitizenPercent = top10(_.citizenWinPercent)
  def bestKomissarPercent = top10(_.komissarWinPercent)
  def bestMafiaPercent = top10(_.mafiaWinPercent)
  def bestCitizenSU = top10(_.citizenAverageSU)
  def bestMafiaSU = top10(_.mafiaAverageSU)
  def bestKomissarSU = top10(_.komissarAverageSU)
  
  private def distribution(op:GameStatistics => collection.immutable.Map[String, Int]) = {
    games.foldLeft(collection.immutable.Map[String, Int]())((acc, game) => mergeMaps(acc, op(game.statistics)))
  }
  
  def mafiaVotesFirstRoundDistribution = distribution(_.mafiaVotesFirstRound)
  
  def citizenVotesFirstRoundDistribution = distribution(_.citizenVotesFirstRound)
  
  def komissarVotesFirstRoundDistribution = distribution(_.komissarVotesFirstRound)
  
  val wordsMap = new Map[String, ListMap[String, Int]]
  
  def mostFrequentWords = toJson(countWords(size = 100, needStem=false, useStopNicks=false, useStopLength=false, useStopWords = false))
  
  def countWords(prefix:String="overall",op:Player => Boolean = (p:Player) => true, size:Int = -1, needStem:Boolean = true, useStopLength:Boolean=true, useStopWords:Boolean = true, useStopNicks:Boolean=true) = {
    val count = wordsMap.get(prefix)
    count match {
      case Some(x) => x
      case None => {
        val filtered = games.map(_.statistics.messageStats.messages).flatten.map(_.normalise(op))
        val words = filtered.flatMap(_.split(" ")).map(_.trim).map(w => RussianStemmer.stem(w, needStem, useStopLength, useStopWords, useStopNicks)).filter(_ != "").foldLeft(Map.empty[String, Int]){
          (count, word) => count + (word -> (count.getOrElse(word, 0) + 1))
        }
        val temp = words.toSeq.sortBy(_._2).filter(_._2 > 8).reverse
        val limited = if(size == -1) temp else temp.take(size)
        val value = ListMap(limited:_*)
        wordsMap.update(prefix, value)
        value
      }
    }

  }
  
  def firstMessageOrderDistribution = {
    val result = messageXOrderDistribution(_.firstMessageFrom(_))
    val mafia = result(Role.MAFIA).groupBy {identity}.map{case (k,v) => k -> v.size}
    val citizen = result(Role.CITIZEN).groupBy {identity}.map{case (k,v) => k -> v.size}
    val kom = result(Role.KOMISSAR).groupBy {identity}.map{case (k,v) => k -> v.size}
    println(mafia)
    println(kom)
    println(citizen)
  }
  
  def secondMessageOrderDistribution = {
    val result = messageXOrderDistribution(_.messageNumberXFrom(2, _))
    val mafia = result(Role.MAFIA).groupBy {identity}.map{case (k,v) => k -> v.size}
    val citizen = result(Role.CITIZEN).groupBy {identity}.map{case (k,v) => k -> v.size}
    val kom = result(Role.KOMISSAR).groupBy {identity}.map{case (k,v) => k -> v.size}
    println(mafia)
    println(kom)
    println(citizen)
  }
  
  def thirdMessageOrderDistribution = {
    val result = messageXOrderDistribution(_.messageNumberXFrom(3, _))
    val mafia = result(Role.MAFIA).groupBy {identity}.map{case (k,v) => k -> v.size}
    val citizen = result(Role.CITIZEN).groupBy {identity}.map{case (k,v) => k -> v.size}
    val kom = result(Role.KOMISSAR).groupBy {identity}.map{case (k,v) => k -> v.size}
    println(mafia)
    println(kom)
    println(citizen)
  }
  
  def timeBetweenMessagesDistribution = {
    println(timeMessageDistribution(_.diffBetweenFirstAndSecondMessages(_)))
  }
  
  def timeBeforeFirstMessageDistribution = {
    println(timeMessageDistribution(_.messageNumberXTime(1, _)))
  }
  
  def timeBeforeSecondMessageDistribution = {
    println(timeMessageDistribution(_.messageNumberXTime(2, _)))
  }
  
  def timeBeforeThirdMessageDistribution = {
    println(timeMessageDistribution(_.messageNumberXTime(3, _)))
  }
  
  def timeBefore4MessageDistribution = {
    println(timeMessageDistribution(_.messageNumberXTime(4, _)))
  }
  
  private def timeMessageDistribution(f: (Round,Player) => Option[Int]) = {
    val distribution = ListBuffer[(Role, Int)]()
    games.foreach(game => {
      val firstRound = game.statistics.firstRound.get
      val times = for(player <- game.players if(f(firstRound, player) != None)) 
        yield player.role -> f(firstRound, player).get
      distribution ++= times
    }
    )
    distribution.groupBy(_._1).map{case (k,v) => k -> v.foldLeft(0)(_+_._2).toDouble / v.size}
  }
  
  private def messageXOrderDistribution(f: (Round,Player) => Option[Message]) = {
    val distribution = ListBuffer[(Role, Int)]()
    games.foreach(game => {
      val firstRound = game.statistics.firstRound.get
      val firstMessages = for(player <- game.players if(f(firstRound, player) != None)) 
        yield firstRound.firstMessageFrom(player)
      val sortedMessages = firstMessages.sortBy(_.get.timeFromStart)
      distribution ++= sortedMessages.zipWithIndex.map{case (k, v) => k.get.fromRole -> (v + 1)}
    }
    )
    distribution.groupBy(_._1).map{case (k,v) => k -> v.foldLeft(ListBuffer[Int]())(_+=_._2)}
  }
  
  def roleMessagesCountFirstKomissarRound = {
    val rolesMessages = games.map{game => {
      val round = game.statistics.firstKomissarRound.get
      for(player <- game.players; role = player.role) 
        yield (role, round.countMessagesFrom(player))
      }
    }.flatten
    val grouped = rolesMessages.groupBy(_._1)
    grouped.map { case (key, value) => (key, value.foldLeft(0)(_+_._2))
    }
  }
  
  def roleMessagesCountFirstMafiaRound = {
    val rolesMessages = games.map{game => {
      val round = game.statistics.firstMafiaRound.get
      for(player <- game.players; role = player.role) 
        yield (role, round.countMessagesFrom(player))
      }
    }.flatten
    val grouped = rolesMessages.groupBy(_._1)
    grouped.map { case (key, value) => (key, value.foldLeft(0)(_+_._2))
    }
  }
  
  def roleMessagesCountFirstRound = {
    val rolesMessages = games.map{game => {
      val round = game.statistics.firstRound.get
      for(player <- game.players; role = player.role) 
        yield (role, round.countMessagesFrom(player))
      }
    }.flatten
    val grouped = rolesMessages.groupBy(_._1)
    grouped.map { case (key, value) => (key, value.foldLeft(0)(_+_._2))
    }
  }
  
  def roleMessagesCountFirstKomissarRoundAverage = {
    val roleMessages = roleMessagesCountFirstKomissarRound
    val overall = roleMessages.foldLeft(0)(_+_._2).toDouble / games.map(_.players).flatten.size
    val citizen = (roleMessages(Role.CITIZEN) + roleMessages(Role.CHILD) + roleMessages(Role.CHILD_GIRL)).toDouble / games.map { _.statistics.playersInRole(_.isCitizen)}.flatten.size
    val kom = roleMessages(Role.KOMISSAR).toDouble / games.map { _.statistics.playersInRole(_.isKomissar)}.flatten.size
    val mafia = roleMessages(Role.MAFIA).toDouble / games.map { _.statistics.playersInRole(_.isMafia)}.flatten.size
    (overall, citizen, kom, mafia)
  }
  
  def roleMessagesCountFirstMafiaRoundAverage = {
    val roleMessages = roleMessagesCountFirstMafiaRound
    val overall = roleMessages.foldLeft(0)(_+_._2).toDouble / games.map(_.players).flatten.size
    val citizen = (roleMessages(Role.CITIZEN) + roleMessages(Role.CHILD) + roleMessages(Role.CHILD_GIRL)).toDouble / games.map { _.statistics.playersInRole(_.isCitizen)}.flatten.size
    val kom = roleMessages(Role.KOMISSAR).toDouble / games.map { _.statistics.playersInRole(_.isKomissar)}.flatten.size
    val mafia = roleMessages(Role.MAFIA).toDouble / games.map { _.statistics.playersInRole(_.isMafia)}.flatten.size
    (overall, citizen, kom, mafia)
  }
  
  def roleMessagesCountFirstRoundAverage = {
    val roleMessages = roleMessagesCountFirstRound
    val overall = roleMessages.foldLeft(0)(_+_._2).toDouble / games.map(_.players).flatten.size
    val citizen = (roleMessages(Role.CITIZEN) + roleMessages(Role.CHILD) + roleMessages(Role.CHILD_GIRL)).toDouble / games.map { _.statistics.playersInRole(_.isCitizen)}.flatten.size
    val kom = roleMessages(Role.KOMISSAR).toDouble / games.map { _.statistics.playersInRole(_.isKomissar)}.flatten.size
    val mafia = roleMessages(Role.MAFIA).toDouble / games.map { _.statistics.playersInRole(_.isMafia)}.flatten.size
    (overall, citizen, kom, mafia)
  }
  
  def countCitizenWords = countWords("citizen", _.isCitizen)
  
  def countMafiaWords = countWords("mafia",_.isMafia)
  
  def countKomissarWords = countWords("kom",_.isKomissar)
  
  def wordsProbability(op: => ListMap[String, Int], percent:Double = 20, occurences:Double = 20) = {
    val count = op
    val overall = countWords()
    var probability = (count.values.sum.toDouble / overall.values.sum) * (1 + percent/100)
    val words = count.filter((t) => t._2 > occurences).map{case (k,v) => k -> v.toDouble / overall(k)}
    val filtered = words.filter((t) => t._2 > probability).map((t) => (t._1 -> (t._2 - probability)))
    ListMap(filtered.toSeq.sortBy(_._2).reverse:_*)
  }
  
  def komissarWordsProbability = wordsProbability(countKomissarWords)
  
  def mafiaWordsProbability = wordsProbability(countMafiaWords)
  
  def citizenWordsProbability = wordsProbability(countCitizenWords)
  
  private def overallVotesFirstRoundDistribution = {
    val mafia = mafiaVotesFirstRoundDistribution
    val citizen = citizenVotesFirstRoundDistribution
    val komissar = komissarVotesFirstRoundDistribution
    val mafcit = (for(key <- citizen.keys) yield (key, mafia.getOrElse(key, 0) + citizen.getOrElse(key, 0))).toMap
    val overall = (for(key <- mafcit.keys) yield (key, komissar.getOrElse(key, 0) + mafcit.getOrElse(key, 0))).toMap
    overall
  }
  
  def mafiaPercentVotesFirstRoundDistribution = percentVotesFirstRound(mafiaVotesFirstRoundDistribution)
  
  private def percentVotesFirstRound(map:collection.immutable.Map[String, Int]) = map.map{case (k,v) => k -> v.toDouble / overallVotesFirstRoundDistribution(k)}
  
  def citizenPercentVotesFirstRoundDistribution = percentVotesFirstRound(citizenVotesFirstRoundDistribution)
  
  def komissarPercentVotesFirstRoundDistribution = percentVotesFirstRound(komissarVotesFirstRoundDistribution)
  
  def komissarCheckedVoteDistribution = {
    val map = new Map[String, Int]
    val rounds = games.foreach(game => {
      val round = game.statistics.firstKomissarRound.get
      if(round.order == 0) {
        map.update("Комиссар посажен на первом ходу", map.getOrElse("Комиссар посажен на первом ходу", 0) + 1)
      } else {
        val prevRound = game.statistics.rounds(round.order - 1)
        val checked = round.checkedPlayer
        val voteOrder = prevRound.voteIndex(checked)
        voteOrder match {
          case -1 => map.update("Не голосовал", map.getOrElse("Не голосовал", 0) + 1)
          case order:Int => map.update(voteOrder.toString, map.getOrElse(voteOrder.toString, 0) + 1)
        } 
      }
    })
    map
  }
  
  def mafiaKilledVoteDistribution = {
    val map = new Map[String, Int]
    val rounds = games.foreach(game => {
      val round = game.statistics.firstMafiaRound.get
      val prevRound = game.statistics.rounds(round.order - 2)
      val checked = round.killedByMafiaPlayer
      if(checked == null) {
        map.update("Промазали мафы", map.getOrElse("Промазали мафы", 0) + 1)
      } else {
        val voteOrder = prevRound.voteIndex(checked)
        voteOrder match {
          case -1 => map.update("Не голосовал", map.getOrElse("Не голосовал", 0) + 1)
          case order:Int => map.update(voteOrder.toString, map.getOrElse(voteOrder.toString, 0) + 1)
        }
      }
      
    })
    map
  }
  
  def maxChecked = top10(_.countChecked)
  
  def maxPrisoned = top10(_.countPrisoned)
  
  def maxKilled = top10(_.countKilled)
  
  def maxCheckedPercent = top10(_.countCheckedPercent)
  
  def maxPrisonedPercent = top10(_.countPrisonedPercent)
  
  def maxKilledPercent = top10(_.countKilledPercent)
  
  def bestAlibi = top10(_.countAlibi)
  
  def bestAlibiPercent = top10(_.countAlibiPercent)
  
  def komissarAliveAverage = {
    games.map(_.statistics.countRoundsAliveKomissar).sum.toDouble / games.size
  }
  
  def checkedDistributionFirstRound = games.filter(_.statistics.firstKomissarRound.isDefined).map(_.statistics.firstKomissarRound.get.checkedRole).groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def checkMafiaProbability = calculateProbability(_.firstKomissarRound, _.map(_.checkMafiaProbability).getOrElse(0.0))
  
  def killKomissarProbability = calculateProbability(_.firstMafiaRound, _.map(_.killKomissarProbability).getOrElse(0.0))
  
  def firstPrisonedMafiaProbability = calculateProbability(_.firstPrisoned, _.map(_.firstPrisonedMafiaProbability).getOrElse(0.0))
  
  private def calculateProbability(roundPred: GameStatistics => Option[Round], op: Option[Round] => Double) = {
    val probabilities = games.map(_.statistics).map(roundPred).map(op).filter(_ > 0)
    probabilities.sum / probabilities.size
  }
  
  //def countKomissarsKilledFirstRound = games.filter(_.statistics.komisarKilledFirstRound.isDefined).size
  
  def firstPrisonedRoundDistribution = games.map(_.statistics.firstPrisonedRole).groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def firstVoteDistribution = games.map(_.statistics.firstVoteTo).groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def mergeMaps(map1:collection.immutable.Map[String, Int], map2:collection.immutable.Map[String, Int]) = map1 ++ map2.map{ case (k,v) => k -> (v + map1.getOrElse(k,0)) }

  def clanOverallPoints(team: Team) = {
    val clan = teamStat(team.name)
    val map = clan.map{case (k, v) => (k, v.points)}
    toJson(map)
  }

  def clanPercents(team: Team) = {
    val clan = teamStatPercent(team.name)
    val json = toJson(clan)
    s"[{name:'Процент очков в команде', data:$json}]"
  }

  def clanFinishStatus(team: Team) = {
    val clan = teamStatFinish(team.name)
    val map = clan.map{case (k, v) => (k.status, v)}
    val json = toJson(map)
    s"[{name:'Как заканчивали игру', data:$json}]"
  }

  def clanPercentGamePoints(team: Team) {
    val clan = teamStat(team.name)
    val map = clan.map{case (k, v) => (k, v.points / v.countPlayed)}
    toJson(map)
  }

  def clanPercentPoints(team: Team) = {
    val clan = teamStat(team.name)
    val map = clan.map{case (k, v) => (k, 100 * v.points / v.possiblePoints)}
    toJson(map)
  }

  def clansuPoints(team: Team) = {
    val clan = teamStat(team.name)
    val map = clan.map{case (k, v) => (k, v.supoints)}
    toJson(map)
  }

  def clanCountPlayed(team: Team) = {
    val clan = teamStat(team.name)
    val map = clan.map{case (k, v) => (k, v.countPlayed)}
    toJson(map)
  }

  def clansuPercent(team: Team) = {
    val clan = teamStat(team.name)
    val map = clan.map{case (k, v) => (k, v.supoints / v.countPlayed)}
    toJson(map)
  }

  def clanStat(team: Team) = {
    val clan = teamStat(team.name)
    clan.values.toList
  }

  def instantMap = {
    val mapped = for {
      (k, v) <- instantStanding
    } yield s"{name:'$k.name',data:[" +v.mkString(",") +"]}"
    val json = mapped.mkString(",")
    s"[$json]"
  }

  def messages = toJson(_messages)

  def messagesRound = toJson(_messagesRound)

  def messagesMinute = toJson(_messagesMinute)
  
  def messagesGame = toJson(_messagesGame)

  def smiles = toJson(_smiles)

  def overallPoints = toJson(_overallPoints)

  def overallsuPoints = toJson(_overallsuPoints)

  def overallPenaltyPoints = toJson(_overallPointsWithPenalty)

  def overallTotalPoints = toJson(_overallPointsWithDrop)

  def overallPercentPoints = toJson(_overallPointsPercent)

  def overallossiblePoints = toJson(_overallPointsPossible)

  def partResults(index:Int) = toJson(partitionPoints(index))

  def possibleResults = TournamentResult.possibleResults(tournament)

  def countResults(result: TournamentResult) = mapResults(result)

  def overallResults = toJson(mapResults.map{case (k, v) => (k.descr, v)})

  def distributionRoles(role: Role) = toJson(mapRoles(role))

  def distributionChecked = toJson(checked)

  def distributionPrisoned = toJson(prisoned)

  def distributionKilledFirst = toJson(killedFirst)

  def distributionCheckedFirst = toJson(checkedFirst)

  def distributionPrisonedFirst = toJson(prisonedFirst)
  
  def distributionBestAlibi = {
    val map = bestAlibi.map(stat => stat.name -> stat.countAlibi).toMap
    val sorted = ListMap(map.toSeq.sortBy(_._2).reverse:_*)
    toJson(sorted)
  }
  
  def distributionBestAlibiPercent = {
    val map = bestAlibiPercent.map(stat => (stat.name -> stat.countAlibiPercent)).toMap
    val sorted = ListMap(map.toSeq.sortBy(_._2).reverse:_*)
    toJson(sorted)
  }

  def distributionKilled = toJson(killed)
  
  def maximumGamesInRole(role:Role) = {
    val maximum = playerStat.map(_.rolesDistribution.getOrElse(role, 0)).max
    playerStat.filter(_.rolesDistribution.getOrElse(role, 0) == maximum)
  }
  
  def percentWinInRole(rolePredicate:Player => Boolean) = {
    val map = teamMapDouble
    teams.foreach(team => {
      val gamesInRole = games.filter(game =>  {
        val exist = for (player <- game.players if(team.containsPlayer(player.name) && rolePredicate(player))) yield game
        !exist.isEmpty
      })
      val value = gamesInRole.map(game => {
        val players = for (player <- game.players if(team.containsPlayer(player.name))) yield player
        game.getWinPoints(players(0))
      })
      if(value != null && value.size > 0){
        map.update(team.name, roundDouble(value.sum/value.size))
      }
    })
    map
  }

  def possibleRoles = Role.posibleRoles(tournament)

  private def update(map: Map[String, Double], value: Double)(implicit teamName: String) = {
    val addon = map.getOrElse(teamName, 0.0)
    map.update(teamName, roundDouble(addon + value))
  }

  private def increment(map: Map[String, Int])(implicit teamName: String) = {
    val count = map.getOrElse(teamName, 0) + 1
    map += (teamName -> count)
  }

  private def incrementIf(condition: => Boolean, map: Map[String, Int])(implicit teamName: String) = {
    if (condition) increment(map)
  }

  private def recalculateMap(map: Map[String, Double]) = {
    map.foreach { case (k, v) => map.update(k, roundDouble(v / countPlayed(k))) }
  }

  private def handleMissedGamePenalties(game: Game, map: Map[String, Double]) = {
    val notApplied = penalties.filter { _.gameId == game.id }.filter { _.player == null }
    notApplied.foreach { pen =>
      {
        implicit val teamName = pen.team.name
        update(map, -pen.points)
        val pointsList = instantStanding.getOrElse(teamName, new ListBuffer[Double]())
        val points = pointsList.lastOption.getOrElse(0.0)
        instantStanding.update(teamName, pointsList += roundDouble(points - pen.points))
      }
    }
  }

  private def calculatePart(part: Part) = {
    calculatePartPoints(part.games.sortBy(_.id))
    if (!last(part)) findTeamsIn(part).foreach(recalculatePointsAfterPartEnded)
  }

  private def handleGame(game: Game, points: Map[String, Double], pointsWithPenalties: Map[String, Double]) = {
    game.players.foreach { handlePlayer(game, _, points, pointsWithPenalties) }
    handleMissedGamePenalties(game, pointsWithPenalties)
  }

  private def recalculatePointsAfterPartEnded(team: String) = {
    tournament match {
      case "Семейгый кубок 2016" => {
        val dropped = dropAfterPart(instantStanding(team).last)
        instantStanding(team) += roundDouble(dropped)
        _overallPointsWithDrop.update(team, roundDouble(dropped))
      }
      case "Межклан 2016" => {
        val value = team match {
          case "Упийцы" | "Лига Выдающихся Мафиози" => 4.0
          case "Боги Мафии" | "Город Грехов" => 3.0
          case "Ночные снайперы" | "Boardwalk Empire" => 2.0
          case "Gangsta Girls" | "Вооруженные силы" => 1.0
          case _ => 0.0 
        }
        instantStanding(team) += roundDouble(value)
        _overallPointsWithDrop.update(team, roundDouble(value))
      }
      case _ => ()
    }
    
  }

  private def findTeamsIn(part: Part) = {
    part.games.flatMap { game => game.players.map { findTeam(_).name } }.distinct
  }

  private def calculatePartPoints(games: List[Game]) = {
    val partitionResult = teamMapDouble
    val partitionResultWithPenalty = teamMapDouble
    games.foreach(handleGame(_, partitionResult, partitionResultWithPenalty))
    partitionPoints += partitionResult
    partitionPointsWithPenalties += partitionResultWithPenalty
  }

  private def dropAfterPart(points: Double) = roundDouble(points / 2)

  private def teamMapListDouble = new Map[String, ListBuffer[Double]]

  private def teamMapDouble = new Map[String, Double]

  private def teamMapInteger = new Map[String, Int]

  private def teamList = ListBuffer[Map[String, Double]]()

  private def findAllGames(parts: List[Part]) = parts.map(_.games).flatten.toList

  private def findTeam(player: Player) = teams.find(_.containsPlayer(player.name))
    .getOrElse(throw new IllegalArgumentException(s"Team must present for player ${player.name}"))

  private def last(current: ListBuffer[Double]) = if (current.size == 0) 0.0 else current(current.size - 1)

  private def handlePlayer(game: Game, player: Player, partResult: Map[String, Double], partResultPenalty: Map[String, Double]) = {
    val team = findTeam(player)
    val stat = game.statistics
    val name = player.name
    implicit val teamName = team.name
    val roundStats = stat.roundStats
    val points = player.points
    val pointsPenalty = penalties.filter(_.gameId == game.id).filter(_.team == team).map(_.points).sum
    val totalPoints = points - pointsPenalty
    val messageStats = stat.messageStats;
    val supoints = player.achievementScore
    val possiblePoints = PointRule.findMaximumPossiblePoints(game, name)
    
    val gamePoints = player.points - pointsPenalty
    val countWin = game.getWinPoints(player)
    val messages = messageStats.countMessagesFrom(player)
    val smiles = messageStats.countSmilesFrom(player)
    val best = game.bestPlayer
    val teamMap = teamStat.getOrElse(teamName, new Map[String, PlayerTournamentStat])
    val playerStat = teamMap.getOrElse(name, new PlayerTournamentStat(name))
    playerStat.update(gamePoints, possiblePoints, supoints, countWin, best == player, messages, smiles, player.role, game, player.status)
    teamMap.update(name, playerStat)
    teamStat.update(teamName, teamMap)
    updateFinishStat(player.status)
    updateRolesStat(player.role)
    updateStandings(totalPoints)
    update(partResult, points)
    update(partResultPenalty, totalPoints)
    update(_overallPointsWithDrop, totalPoints)
    update(_overallPoints, points)
    update(_overallPointsWithPenalty, totalPoints)
    update(_overallPointsPossible, possiblePoints)
    update(_overallsuPoints, supoints)
    update(_messages, messages)
    update(_smiles, smiles)
    update(_messagesRound, messageStats.avgMessagesRoundForPlayer(player))
    update(_messagesMinute, messageStats.messagesPerMinute(player))
    update(_messagesGame, messageStats.countMessagesForPlayer(player))
    increment(countPlayed)
    messageStats.rounds.map(_.smileMessages).flatten.toList.foreach { increment(smilesMap)(_) }
    incrementIf(roundStats.killed(player), killed)
    incrementIf(roundStats.checked(player), checked)
    incrementIf(roundStats.prisoned(player), prisoned)
    incrementIf(roundStats.killedFirst(player), killedFirst)
    incrementIf(roundStats.checkedFirst(player), checkedFirst)
    incrementIf(roundStats.prisonedFirst(player), prisonedFirst)
  }

  private def calculatePercentValues(team: Team) = {
    val name = team.name
    _overallPointsPercent.update(name, roundDouble(_overallPoints(name) / _overallPointsPossible(name) * 100))
    val overall = _overallPoints(name)
    team.players.foreach(player => {
      val points = teamStat(name).getOrElse(player.name, new PlayerTournamentStat(player.name)).points
      val teamMap = teamStatPercent.getOrElse(name, new Map)
      teamMap += player.name -> 100 * points / overall
      teamStatPercent.update(name, teamMap)
    });
  }

  private def calculate = {
    parts.foreach(calculatePart)
    teams.foreach(calculatePercentValues)
    recalculateMap(_messagesRound)
    recalculateMap(_messagesMinute)
    recalculateMap(_messagesGame)
  }
  
  private def playersSortedBy(op: PlayerTournamentStat => Double) = playerStat.filter(_.countPlayed > 3).sortBy(op)
  
  private def firstTen(players:List[PlayerTournamentStat]) = players.reverse.take(10).toList
  
  private def top10(op: PlayerTournamentStat => Double) = firstTen(playersSortedBy(op))
  
  private def toJson(map: collection.Map[_ <: Any, _ <: Any]) = {
    val json = for {
      (key, value) <- map
    } yield s"['$key', $value]"
    val result = json.mkString(",")
    s"[$result]"
  }

  private def updateFinishStat(status:FinishStatus)(implicit teamName:String) = {
    val team = teamStatFinish.getOrElse(teamName, new Map[FinishStatus, Int])
    val map = team.getOrElse(status, 0)
    team.update(status, map + 1)
    teamStatFinish.update(teamName, team)
  }
  
  private def updateRolesStat(role:Role)(implicit teamName:String) = {
    val team = mapRoles.getOrElse(role, new Map[String, Double])
    update(team, 1)
    mapRoles.update(role, team)
  }

  private def updateStandings(points:Double)(implicit teamName:String) = {
    val team = instantStanding.getOrElse(teamName, new ListBuffer[Double]())
    val lastValue = team.lastOption.getOrElse(0.0)
    team += roundDouble(points + lastValue)
    instantStanding.update(teamName, team)
  }
  
  private def last(part:Part) = parts.last == part
}