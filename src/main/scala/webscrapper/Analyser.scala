package webscrapper

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import webscrapper.stats.GameStatistics
import scala.collection.immutable.ListMap
import webscrapper.util.RussianStemmer
import webscrapper.util.StatUtils._

class Analyser(val games:List[Game], val name:String = "Default") {
  type Map[T, E] = mutable.HashMap[T, E]

  val mapResults: collection.Map[TournamentResult, Int] = mapToSize(games.groupBy( _.tournamentResult))
  val longest = games.maxBy(_.duration)
  val shortest = games.minBy(_.duration)
  val pointRules = PointRule.get(this.name)
  val playerStats = new Map[String, PlayerTournamentStat]
  
  calculate
  def mapResultsPercent = {
    val a = mapResults
    val size = a.values.sum
    a.map{case (k,v) => k  -> v.toDouble / size *100}
  }
  val mapResultsChildsPrisonedFirst = {
    val g = games.filter(_.statistics.childPrisonedFirst)
    val a = mapToSize(g.groupBy( _.tournamentResult))
    val size = a.values.sum
    a.map{case (k,v) => k  -> v.toDouble / size *100}
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
  def maxChecked = top10(_.countChecked)
  def maxPrisoned = top10(_.countPrisoned)
  def maxKilled = top10(_.countKilled)
  def maxCheckedPercent = top10(_.countCheckedPercent)
  def maxPrisonedPercent = top10(_.countPrisonedPercent)
  def maxKilledPercent = top10(_.countKilledPercent)
  def bestAlibi = top10(_.countAlibi)
  def bestAlibiPercent = top10(_.countAlibiPercent)
  
  def mafiaPairsOrderDistribution = {
    games.map(_.statistics).map(_.mafiaPairs).groupBy(identity).map{case (k,v) => k -> v.size}
  }
  
  def mafiaOrderDistribution = distribution(_.rolesOrder(_.isMafia))
  def komissarOrderDistribution = distribution(_.rolesOrder(_.isKomissar))
  def childOrderDistribution = distribution(_.rolesOrder(_.isChild))
  def citizenOrderDistribution = distribution(_.rolesOrder(_.isCitizenRaw))
  
  def mafiaVotesFirstRoundDistribution = distribution(_.mafiaVotesFirstRound)
  def citizenVotesFirstRoundDistribution = distribution(_.citizenVotesFirstRound)
  def komissarVotesFirstRoundDistribution = distribution(_.komissarVotesFirstRound)
  def maniacFirstKillDistribution = distribution(_.maniacKilledFirstRound)
  def doctorFirstSaveDistribution = distribution(_.doctorSavedFirstRound)
  def komissarFirstCheckDistribution = distribution(_.komissarVotesFirstRoundRole)
  def bossFirstFreezeDistribution = distribution(_.bossVotesFirstRound)
  def doctorFirstAttemptDistribution = distribution(_.doctorAttemptedFirstRound)
  
  private def roleWinProbability(op:Game => Int) = {
    val mapped = games.map { g => 
      val roundsAlive = op
      (roundsAlive -> g.result)
    }
    val groupped = mapped.groupBy(_._1).map{case (k,v) => k -> v.map(_._2).groupBy(identity).map{case (k1, v1) => k1 -> v1.size}}
    val result = groupped.map(g =>{
      val map = g._2
      val size = map.values.sum
      val mapped = map.map{case (k, v) => k -> v.toDouble / size * 100 }
      g._1 -> mapped
    }
    )
    result
  }
  
  def maniacWinProbability = roleWinProbability(_.statistics.countManiacRounds)
  
  def komissarWinProbability = roleWinProbability(_.statistics.countRoundsAliveKomissar)
  
  def serzhantWinProbability = roleWinProbability(_.statistics.countRoundsAliveSerzhant)
  
  def bossWinProbability = roleWinProbability(_.statistics.countRoundsAliveBoss)
  
  def doctorWinProbability = roleWinProbability(_.statistics.countDoctorRounds)
  
  val wordsMap = new Map[String, ListMap[String, Int]]
  
  def mostFrequentWords = toJson(countWords(size = 100, needStem=false, useStopNicks=false, useStopLength=false, useStopWords = false))
  
  def firstMessageOrderDistribution = messageOrderDistribution(1)
  
  def secondMessageOrderDistribution = messageOrderDistribution(2)
  
  def thirdMessageOrderDistribution = messageOrderDistribution(3)
  
  def timeBetweenMessagesDistribution = timeMessageDistribution(_.diffBetweenFirstAndSecondMessages(_))
  
  def timeBeforeFirstMessageDistribution = timeMessageDistribution(_.messageNumberXTime(1, _))
  
  def timeBeforeSecondMessageDistribution = timeMessageDistribution(_.messageNumberXTime(2, _))
  
  def timeBeforeThirdMessageDistribution = timeMessageDistribution(_.messageNumberXTime(3, _))
  
  def timeBefore4MessageDistribution = timeMessageDistribution(_.messageNumberXTime(4, _))
  
  def roleMessagesCountFirstKomissarRound = roleMessages(_.firstKomissarRound.get)
  
  def roleMessagesCountFirstMafiaRound = roleMessages(_.firstMafiaRound.get)
  
  def roleMessagesCountFirstRound = roleMessages(_.firstRound.get)
  
  def roleMessagesCountFirstKomissarRoundAverage = roleMessagesAverage(roleMessagesCountFirstKomissarRound)
  
  def roleMessagesCountFirstMafiaRoundAverage = roleMessagesAverage(roleMessagesCountFirstMafiaRound)
  
  def roleMessagesCountFirstRoundAverage = roleMessagesAverage(roleMessagesCountFirstRound)
  
  def countCitizenWords = countWords("citizen", _.isCitizen)
  
  def countMafiaWords = countWords("mafia",_.isMafia)
  
  def countKomissarWords = countWords("kom",_.isKomissar)
  
  def komissarWordsProbability = wordsProbability(countKomissarWords)
  
  def mafiaWordsProbability = wordsProbability(countMafiaWords)
  
  def citizenWordsProbability = wordsProbability(countCitizenWords)
  
  def mafiaPercentVotesFirstRoundDistribution = percentVotesFirstRound(mafiaVotesFirstRoundDistribution)
  
  def citizenPercentVotesFirstRoundDistribution = percentVotesFirstRound(citizenVotesFirstRoundDistribution)
  
  def komissarPercentVotesFirstRoundDistribution = percentVotesFirstRound(komissarVotesFirstRoundDistribution)
  
  def komissarCheckedVoteDistribution = voteDistribution(_.firstKomissarRound.get, _.checkedPlayer.get)
  
  def mafiaKilledVoteDistribution = voteDistribution(_.firstMafiaRound.get, _.killedByMafiaPlayer.get)
  
  def komissarAliveAverage = games.map(_.statistics.countRoundsAliveKomissar).sum.toDouble / games.size
  def maniacAliveAverage = games.map(_.statistics.countManiacRounds).sum.toDouble / games.size
  def serzhantAliveAverage = games.map(_.statistics.countRoundsAliveSerzhant).sum.toDouble / games.size
  def bossAliveAverage = games.map(_.statistics.countRoundsAliveBoss).sum.toDouble / games.size
  
  def doctorAliveAverage = games.map(_.statistics.countDoctorRounds).sum.toDouble / games.size
  def maniacCountKillsAverage = games.map(_.statistics.countManiacKills).sum.toDouble / games.size
  def doctorCountSavesAverage = games.map(_.statistics.countDoctorSaves).sum.toDouble / games.size
  def bossCountFreezeAverage = games.map(_.statistics.countFreezes).sum.toDouble / games.size
  def komissarCountChecksAverage = games.map(_.statistics.countChecks).sum.toDouble / games.size
  def checkedDistributionFirstRound = mapToSize(games.map(_.statistics.firstKomissarRound.map(_.checkedRole)).groupBy(identity))
  
  //def rolesOrderDistribution = mapToSize(games.map(_.statistics.firstKomissarRound.checkedRole).groupBy(identity))
  
  def checkMafiaProbability = calculateProbability(_.firstKomissarRound, _.map(_.checkMafiaProbability).getOrElse(0.0))
  
  def killKomissarProbability = calculateProbability(_.firstMafiaRound, _.map(_.killKomissarProbability).getOrElse(0.0))
  
  def firstPrisonedMafiaProbability = calculateProbability(_.firstPrisoned, _.map(_.firstPrisonedMafiaProbability).getOrElse(0.0))
  
  //def countKomissarsKilledFirstRound = games.filter(_.statistics.komisarKilledFirstRound.isDefined).size
  
  def countMafiaCheckedFirstRound = games.filter(_.statistics.mafiaCheckedFirstRound.isDefined).size
  
  def countMafiaPrisonedFirstRound = games.filter(_.statistics.mafiaPrisonedFirstRound.isDefined).size
  
  def countGames = games.size
  
  def firstPrisonedRoundDistribution = mapToSize(games.map(_.statistics.firstPrisonedRole).groupBy(identity))
  
  def firstVoteDistribution = mapToSize(games.map(_.statistics.firstVoteTo).groupBy(identity))
  
  def countResults(result: TournamentResult) = mapResults(result)

  def overallResults = toJson(mapResults.map{case (k, v) => (k.descr, v)})
  
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
  
  def maximumGamesInRole(role:Role) = {
    val maximum = playerStats.map(_._2).map(_.rolesDistribution.getOrElse(role, 0)).max
    playerStats.map(_._2).filter(_.rolesDistribution.getOrElse(role, 0) == maximum)
  }
  
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

  private def mergeMaps(map1:collection.immutable.Map[String, Int], map2:collection.immutable.Map[String, Int]) = map1 ++ map2.map{ case (k,v) => k -> (v + map1.getOrElse(k,0)) }

  private def calculateProbability(roundPred: GameStatistics => Option[Round], op: Option[Round] => Double) = {
    val probabilities = games.map(_.statistics).map(roundPred).map(op).filter(_ > 0)
    probabilities.sum / probabilities.size
  }
    
  private def handleGame(game: Game) = game.players.foreach(handlePlayer(game, _))
  
  private def last(current: ListBuffer[Double]) = if (current.size == 0) 0.0 else current(current.size - 1)

  private def handlePlayer(game: Game, player: Player) = {
    val stat = game.statistics
    val name = player.name
    val roundStats = stat.roundStats
    val points = player.points
    //val pointsPenalty = penalties.filter(_.gameId == game.id).filter(_.team == team).map(_.points).sum
    //val totalPoints = points - pointsPenalty
    val totalPoints = points
    val messageStats = stat.messageStats;
    val supoints = player.achievementScore
    //val possiblePoints = PointRule.findMaximumPossiblePoints(game, name)
    val possiblePoints = 2.0
    
    //val gamePoints = player.points - pointsPenalty
    val gamePoints = player.points
    val countWin = game.getWinPoints(player)
    val messages = messageStats.countMessagesFrom(player)
    val smiles = messageStats.countSmilesFrom(player)
    val best = game.bestPlayer

    val playerStat = playerStats.getOrElse(name, new PlayerTournamentStat(name))
    playerStat.update(gamePoints, possiblePoints, supoints, countWin, best == player, messages, smiles, player.role, game, player.status)
    playerStats.update(name, playerStat)
  }

  private def calculate = games foreach handleGame
  
  private def playersSortedBy(op: PlayerTournamentStat => Double) = playerStats.map(_._2).filter(_.countPlayed > 3).toList.sortBy(op)
  
  private def firstTen(players:List[PlayerTournamentStat]) = players.reverse.take(10).toList
  
  private def top10(op: PlayerTournamentStat => Double) = firstTen(playersSortedBy(op))
  
  private def distribution(op:GameStatistics => collection.immutable.Map[String, Int]) = {
    games.foldLeft(collection.immutable.Map[String, Int]())((acc, game) => mergeMaps(acc, op(game.statistics)))
  }
  
  private def distributionFiltered(op:GameStatistics => collection.immutable.Map[String, Int]) = {
    games.filter(_.statistics.citizenRoundsBeforePrisoned.size > 1).foldLeft(collection.immutable.Map[String, Int]())((acc, game) => mergeMaps(acc, op(game.statistics)))
  }
  
  private def messageOrderDistribution(order:Int) = {
    val result = messageXOrderDistribution(_.messageNumberXFrom(order, _))
    val mafia = mapToSize(result(Role.MAFIA).groupBy {identity})
    val citizen = mapToSize(result(Role.CITIZEN).groupBy {identity})
    val kom = mapToSize(result(Role.KOMISSAR).groupBy {identity})
    (mafia, kom, citizen)
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
    distribution.groupBy(_._1).map{case (k,v) => k -> v.foldLeft(ListBuffer[Int]())(_+=_._2).toList}
  }
  
  private def roleMessages(roundOp:GameStatistics => Round) = {
    val rolesMessages = games.map{game => {
      val round = roundOp(game.statistics)
      for(player <- game.players; role = player.role) 
        yield (role, round.countMessagesFrom(player))
      }
    }.flatten
    val grouped = rolesMessages.groupBy(_._1)
    grouped.map { case (key, value) => (key, value.foldLeft(0)(_+_._2))
    }
  }
  
  private def roleMessagesAverage(roleMessages:scala.collection.immutable.Map[Role, Int]) = {
    val overall = roleMessages.foldLeft(0)(_+_._2).toDouble / games.map(_.players).flatten.size
    val citizen = (roleMessages(Role.CITIZEN) + roleMessages.getOrElse(Role.CHILD, 0) + roleMessages.getOrElse(Role.CHILD_GIRL, 0) + roleMessages.getOrElse(Role.CHILD_UNKNOWN, 0)).toDouble / games.map { _.statistics.playersInRole(_.isCitizen)}.flatten.size
    val kom = roleMessages(Role.KOMISSAR).toDouble / games.map { _.statistics.playersInRole(_.isKomissar)}.flatten.size
    val mafia = roleMessages(Role.MAFIA).toDouble / games.map { _.statistics.playersInRole(_.isMafia)}.flatten.size
    (overall, citizen, kom, mafia)
  }
  
  private def wordsProbability(op: => ListMap[String, Int], percent:Double = 20, occurences:Double = 20) = {
    val count = op
    val overall = countWords()
    var probability = (count.values.sum.toDouble / overall.values.sum) * (1 + percent/100)
    val words = count.filter((t) => t._2 > occurences).map{case (k,v) => k -> v.toDouble / overall(k)}
    val filtered = words.filter((t) => t._2 > probability).map((t) => (t._1 -> (t._2 - probability)))
    ListMap(filtered.toSeq.sortBy(_._2).reverse:_*)
  }
  
  private def overallVotesFirstRoundDistribution = {
    val mafia = mafiaVotesFirstRoundDistribution
    val citizen = citizenVotesFirstRoundDistribution
    val komissar = komissarVotesFirstRoundDistribution
    val mafcit = (for(key <- citizen.keys) yield (key, mafia.getOrElse(key, 0) + citizen.getOrElse(key, 0))).toMap
    val overall = (for(key <- mafcit.keys) yield (key, komissar.getOrElse(key, 0) + mafcit.getOrElse(key, 0))).toMap
    overall
  }
  
  private def voteDistribution(roundOp:GameStatistics => Round, playerOp:Round => Player) = {
    val map = new Map[String, Int]
    val rounds = games.foreach(game => {
      //CHECK THAT ROUND AND PLAYER NOT NULL
      val round = roundOp(game.statistics)
      
      val player = playerOp(round)
      if(round.order == 0) {
        map.update("Комиссар посажен на первом ходу", map.getOrElse("Комиссар посажен на первом ходу", 0) + 1)
      } else {
        val prevRound = game.statistics.rounds(round.order - 1)
        val voteOrder = prevRound.voteIndex(player)
        voteOrder match {
          case -1 => map.update("Не голосовал", map.getOrElse("Не голосовал", 0) + 1)
          case order:Int => map.update(voteOrder.toString, map.getOrElse(voteOrder.toString, 0) + 1)
        } 
      }
    })
    map
  }
  
  private def percentVotesFirstRound(map:collection.immutable.Map[String, Int]) = map.map{case (k,v) => k -> v.toDouble / overallVotesFirstRoundDistribution(k)}
    
  private def toJson(map: collection.Map[_ <: Any, _ <: Any]) = {
    val json = for {
      (key, value) <- map
    } yield s"['$key', $value]"
    val result = json.mkString(",")
    s"[$result]"
  }
}