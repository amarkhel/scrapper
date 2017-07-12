package webscrapper.stats

import webscrapper.Player
import java.time.LocalDateTime
import webscrapper.Round
import scala.collection.mutable.ListBuffer
import java.time.temporal.ChronoUnit
import webscrapper.Role
import webscrapper.RoundType
import webscrapper.RoundType._
import webscrapper.FinishStatus
import webscrapper.FinishStatus._
import scala.io.Source
import webscrapper.util.RussianStemmer
import webscrapper.PointsCalcStrategy
import webscrapper.rules.RuleEngine

class GameStatistics(val rounds: List[Round], val players: List[Player], val start: LocalDateTime, val finish: LocalDateTime) {
  require(rounds != null && rounds.size > 1)
  require(players != null && players.size > 0)
  require(start != null && finish != null && finish.isAfter(start))
  val duration: Long = start.until(finish, ChronoUnit.SECONDS);
  val messageStats = new MessageStatistics(players, rounds, duration)
  val voteStats = new VoteStatistics(players, rounds)
  val roundStats = new RoundStatistics(rounds)
  val pointsStats = new PointsStatistics(players)
  val alived = players.filter(_.status == ALIVE)

  def omonRound = {
    citizenRounds.sortBy(_.order).find(_.wasOmon)
  }
  
  def mafiaPairs = {
    val mafia = players.filter(_.isMafia)
    for (maf <- mafia ) yield players.indexOf(maf)
  }
  
/*  def xcombinations(list:List[Player], n: Int): List[List[String]] =
  if (n > xsize) Nil
  else l match {
    case _ :: _ if n == 1 =>
      l.map(List(_))
    case hd :: tl =>
      tl.xcombinations(n - 1).map(hd :: _) ::: tl.xcombinations(n)
    case _ => Nil
  }*/
  
  def mafiaPairsNames = {
    val mafia = players.filter(_.isMafia).sortBy(_.name)
    def pairs(acc:ListBuffer[(String, String)], players:List[Player]) : List[(String, String)] = {
      players match {
        case (head :: tail) => {
          val p = for (maf <- tail) yield (head.name, maf.name)
          acc ++=(p)
          pairs(acc, tail)
        }
        case _ => acc.toList
      }
    }
    pairs(new ListBuffer[(String, String)](), mafia)
  }
  
  def komPairs = {
    if (this.playersInRole(_.isKomissar).isEmpty || this.playersInRole(_.isSerzhant).isEmpty) None else {
      val kom = this.playersInRole(_.isMainKomissar)(0).name
      val serzh = this.playersInRole(_.isSerzhant)(0).name
      val list = new ListBuffer[String]()
      list += kom
      list+= serzh
      val sorted = list.sortBy(identity).toList
      Some(sorted(0), sorted(1))
    }
  }
  
  def findPlayer(name: String) = {
    require(name != null)
    players.find(_.name == name)
  }
  
  def playerInRole(name:String, predicate: Player => Boolean) = {
    val player = findPlayer(name)
    if(player.isDefined) predicate(player.get) else false
  }
  
  def playersInRole(predicate: Player => Boolean) = {
    players.filter(predicate)
  }

  def probabilityRole = {
    val citizen = Source.fromFile("citizen.txt", "UTF-8").getLines().map(_.split(":")).map { words => (words(0).trim -> words(1).trim) }.toMap
    val maf = Source.fromFile("maf.txt", "UTF-8").getLines().map(_.split(":")).map { words => (words(0).trim -> words(1).trim) }.toMap
    val kom = Source.fromFile("kom.txt", "UTF-8").getLines().map(_.split(":")).map { words => (words(0).trim -> words(1).trim) }.toMap
    for (player <- players) {
      val name = player.name
      var m = 0.0
      var c = 0.0
/*      var k = 0.0*/
      var countC = 0
      var countM = 0
      //var countK = 0
      val words = messageStats.messages.filter(_.from(player)).map { _.normalise() }.flatMap(_.split(" ")).map(RussianStemmer.stem(_))
      words.foreach { word =>
        {
          if (citizen.contains(word)) c += citizen(word).toDouble
          else if (maf.contains(word)) m += maf(word).toDouble
          else if (kom.contains(word)) c += kom(word).toDouble
          if (citizen.contains(word)) countC+=1
          else if (maf.contains(word)) countM+=1
          else if (kom.contains(word)) countC+=1
        }
      }
      val log = m/c
      val log2 = countM.toDouble/countC
      println(s"$name - $c $m $log $countC $countM $log2")
    } 
  }

  def countAlibiVotes(player: Player) = {
    voteStats.countCitizenVotesToMafia(player)
  }
  
  def countOtvetka(player: Player) = {
    voteStats.countOtvetka(player)
  }

  def countAlived = alived.size

  /**
   * @param number - number of the round
   * @return requested round.
   * @throws IllegalArgumentException if parameter number is incorrect
   */
  private[webscrapper] def round(number: Int) = {
    require(number < countRounds && number >= 0)
    rounds(number)
  }

  def countRounds = rounds.size

  def countPlayers = players.size

  /**
   * @param player
   * @return true if specified player not killed,
   *  timeouted or prisoned during the game
   */
  def alive(player: Player) = {
    require(player != null && players.contains(player))
    alived contains player
  }

  /**
   * @return true if at least one player leaved game by timeout
   */
  
  def timeouters = rounds.filter(_.hasTimeout).flatMap(_.timeouts)
  
  def hasTimeout = has(countRounds(_.hasTimeout))

  def doctorSavedCount = countRounds(_.hasDoctorSaved)
  
  def doctorSaved = rounds.filter(_.hasDoctorSaved).map(_.doctorSavedPlayerRole)

  def wasPrisoned(player: Player) = has(countRounds(_.isPrisoned(player)))

  def wasChecked(player: Player) = has(countRounds(_.checked(player)))

  def wasKilled(player: Player) = has(countRounds(_.killedByMafia(player)))

  def wasTimeout(player: Player) = has(countRounds(_.wasTimeout(player)))

  def lastAlive(player: Player) = lastRound.lastAlive(player)

  def killedFirstRound(player: Player) = firstMafiaRound.get.hasKills && firstMafiaRound.get.killed.toList.contains(player)

  def countSurvivedCitizenRounds(player: Player) = countRounds(_.wasNotPrisoned(player))

  def missedSavings(player: Player) = countFor(player.isDoctor, _.doctorMissedVote)

  def missedManiacKills(player: Player) = countFor(player.isManiac, _.maniacMissedKills)

  def missedFreezes(player: Player) = countFor(player.isBoss, _.hasMissedMoroz)

  def missedKills(player: Player) = countForRounds(player.isMafia, count(mafiaRounds.filter { _.alived(player) }, !_.hasVoteFrom(player)))

  def missedChecks(player: Player) = countFor(player.isKomissar, _.missedCheck(player))

  def doctorAttemptsToMafia(player: Player) = countFor(player.isDoctor, _.doctorAttemptToMafia)

  def doctorAttemptsToGoodRoles(player: Player) = countFor(player.isDoctor, _.doctorAttemptToImportantGoodRole)

  def checkedRoles(player: Player) = countFor(player.isKomissar, _.checkedRole(player))

  def countFrozenRoles(player: Player) = {
    require(player.isBoss)
    rounds.filter(r => r.getFrozen.isDefined && r.getFrozen.get.isRole).map(_.getFrozen).distinct.toSet.size
  }

  def maniacKilledRoles(player: Player) = countFor(player.isManiac, _.maniacKilledRole)

  def maniacDuplets(player: Player) = countFor(player.isManiac, _.hasKillDuplet)

  def doctorSavedTimes(player: Player) = countFor(player.isDoctor, _.hasDoctorSaved)

  def killedByManiac(player: Player) = countFor(player.isMafia, _.killedByManiac(player)) > 0

  def doctorAttemptsTo(player: Player) = countFor(player.isMafia, _.doctorAttempTo(player))

  def maniacKilledRoleFirstRound(player: Player) = firstManiacKill.isDefined && firstManiacKill.get.isRole

  def doctorAttemptToRoleFirstRound(player: Player) = firstMafiaRound.get.doctorAttemptToImportantGoodRole && player.isDoctor

  def doctorSavedFirstRound(player: Player) = firstMafiaRound.get.hasDoctorSaved && player.isDoctor

  def freezeRoleFirstRound(player: Player) = firstMafiaRound.get.hasFrozenRole && player.isManiac

  def checkMafiaFirstRound(player: Player) = firstKomissarRound.isDefined && firstKomissarRound.get.checkedMafia(player)

  def checkRoleFirstRound(player: Player) = firstKomissarRound.isDefined && firstKomissarRound.get.checkedRole(player)

  def countManiacKills = countRounds(_.hasKilledByManiac)
  
  def countDoctorSaves = countRounds(_.hasSavedByDoctor)
  
  def countChecks = countRounds(_.hasCheck)
  
  def countFreezes = countRounds(_.hasMoroz)

  private def sortedRounds = rounds.sortBy(_.order)

  private def mafiaRounds = filteredByType(MAFIA)
  
  private def maniacRounds = mafiaRounds.filter(_.hasManiac)
  
  private def doctorRounds = mafiaRounds.filter(_.hasDoctor)

  private def citizenRounds = filteredByType(CITIZEN)
  
  private def roundsWithChecks = filteredByType(KOMISSAR)
  
  private def prisonersRounds = citizenRounds.filter(_.hasPrisoners)

  private def komissarRounds = roundsWithChecks.filter(_.hasKomissar)
  
  private def serzhantRounds = roundsWithChecks.filter(_.hasSerzhant)

  private def bossRounds = filteredByType(BOSS)
  
  def aliveTill(name:String) = {
    val pl = findPlayer(name).get
    val lastRound = rounds.filter(_.alived(pl)).sortBy(_.order).last
    val order = lastRound.order
    val lastCitRound = prisonersRounds.filter(_.order <= order).size
    (lastCitRound, order)
  }
  
  def countManiacRounds = maniacRounds.size
  
  def countDoctorRounds = doctorRounds.size

  private def filteredByType(roundType: RoundType) = sortedRounds.filter(_.typeIs(roundType))

  private def first(rounds: List[Round], predicate: Round => Boolean = (round) => true) = {
    rounds match {
      case Nil => None
      case rounds if predicate(rounds(0)) => Some(rounds(0))
    }
  }

  def citizenRoundsBeforePrisoned = {
    val order = citizenRounds.sortBy(_.order).find(_.hasPrisoners).get.order
    citizenRounds.sortBy(_.order).takeWhile(_.order <= order).filter(_.countVotes > 5)
  }

  def countRoundsAliveKomissar = komissarRounds.size
  
  def countRoundsAliveSerzhant = serzhantRounds.size
  
  def countRoundsAliveBoss = bossRounds.size

  def citizenRoundsBeforePrisonedVotes(player: Player) = {
    player.role match {
      case Role.MAFIA => Nil
      case _ => {
        if(citizenRounds.sortBy(_.order).find(_.hasPrisoners).isDefined) {
          val order = citizenRounds.sortBy(_.order).find(_.hasPrisoners).get.order
          citizenRounds.sortBy(_.order).takeWhile(_.order <= order).filter(_.hasVoteFrom(player))
        } else {
          Nil
        }
        
      }
    }
  }

  def citizenRoundsWithVotes(player: Player) = {
    player.role match {
      case Role.MAFIA => Nil
      case _ => citizenRounds.sortBy(_.order).filter(_.hasVoteFrom(player))
    }
  }

  def firstKomissarRound = first(komissarRounds)
  
  def firstBossRound = first(bossRounds)
  
  def firstManiacRound = first(mafiaRounds.filter(_.alived(playersInRole {_.isManiac}(0))))

  def firstRound = first(rounds)
  def firstMafiaRound = first(mafiaRounds)

  def rolesOrder(rolePredicate:Player => Boolean) = citizenRounds.sortBy(_.order).takeWhile(_.order < 2).map(_.rolesOrder(rolePredicate)).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def mafiaVotesFirstRound = List(citizenRounds(0)).map(_.mafiaVotesOrder).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }

  def citizenVotesFirstRound = List(citizenRounds(0)).map(_.citizenVotesOrder).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }

  def komissarVotesFirstRound = List(citizenRounds(0)).map(_.komissarVotesOrder).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def maniacKilledFirstRound = first(maniacRounds).map(_.killedByManiacPlayerRole).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def doctorSavedFirstRound = first(doctorRounds).map(_.doctorSavedPlayerRoleRaw).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def doctorAttemptedFirstRound = first(doctorRounds).map(_.doctorAttemptedPlayerRole).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }

  def doctorAttemptedFirstRoundPlayer = first(doctorRounds).flatMap(_.doctorAttemptedPlayer)
  
  def bossVotesFirstRound = first(bossRounds).map(_.bossFreezeRole).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }
  
  def komissarVotesFirstRoundRole = first(komissarRounds).map(_.komissarCheckRole).flatten.groupBy(identity).map { case (key, value) => (key, value.size) }
  
  //def komisarKilledFirstRound = firstMafiaRound.map(g => g.killed == Role.KOMISSAR.role)
  
  def mafiaCheckedFirstRound = firstKomissarRound.map(_.checkedRole == Role.MAFIA.role)
  
  def mafiaPrisonedFirstRound = firstPrisoned.map(_.prisonerRole == Role.MAFIA.role)

  def firstPrisonedRole = firstPrisoned.map(_.prisonerRole)
  
  def firstPrisonedName = firstPrisoned.map(_.prisonerName)
  
  def firstMorozRole = first(bossRounds).flatMap(_.morozRole)
  
  def firstMoroz = first(bossRounds).flatMap(_.morozName)
  
  def firstMafiaKill = first(mafiaRounds).flatMap(_.killedMafia)
  
  def firstManiacKill = first(mafiaRounds).flatMap(_.killedManiac)
  
  def firstPrisoned = first(citizenRounds.filter(_.hasPrisoners))

  def firstVoteTo = first(citizenRounds).map(_.firstNotMafiaVoteTo).flatten

  private def lastRound = rounds(rounds.size - 1)

  private def wasAliveIn(player: Player, round: RoundType) = sortedRounds.filter(_.wasAliveIn(round, player))

  private def countRounds(predicate: Round => Boolean) = rounds.filter(predicate).size

  private def count(rounds: List[Round], predicate: Round => Boolean) = rounds.filter(predicate).size

  private def countFor(playerPredicate: => Boolean, roundPredicate: Round => Boolean) = {
    playerPredicate match {
      case true => countRounds(roundPredicate)
      case false => 0
    }
  }
  
  def childPrisonedFirst = {
    val role = firstPrisonedRole.get
    role == Role.CHILD.role || role == Role.CHILD_GIRL.role || role == Role.CHILD_UNKNOWN.role
  }
  
  def wasTroika = citizenRounds.last.countPlayers == 3 && citizenRounds.last.alivePlayersCount(!_.isMafia) == 2
  
  def alivedOnTroika = {
    citizenRounds.last.alivedPlayers
  }
  
  def wasAliveOnTroika(name:String) = citizenRounds.last.alived(findPlayer(name))

  def wasLongTroika = {
    if(wasTroika && prisonersRounds.size > 0){
    val last = prisonersRounds.last.order
    val prev = prisonersRounds(prisonersRounds.size - 2).order
    val prevIndex = citizenRounds.sortBy(_.order).find(r => r.order >= prev && r.order <=last && r.countVotes == 2)
    prevIndex match {
      case None => false
      case Some(round) => {
        val ind = round.order
        val count = citizenRounds.sortBy(_.order).filter(r => r.order >= ind && r.order <=last).size
        count > 2
      }
    }
    } else false
  }
  
  def countTroikaRounds = {
    val last = prisonersRounds.last.order
    val prev = prisonersRounds(prisonersRounds.size - 2).order
    val prevIndex = citizenRounds.sortBy(_.order).find(r => r.order >= prev && r.order <=last && r.countVotes ==2).get.order
    val count = citizenRounds.sortBy(_.order).filter(r => r.order >= prevIndex && r.order <=last).size  
    count
  }
  
  private def countForRounds(playerPredicate: => Boolean, roundPredicate: => Int) = {
    playerPredicate match {
      case true => roundPredicate
      case false => 0
    }
  }

  def has(count: Int) = count > 0
}