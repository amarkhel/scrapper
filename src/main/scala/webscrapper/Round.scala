package webscrapper

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import scala.collection.mutable.ListBuffer
import webscrapper.util.TimeUtils._
import webscrapper.util.TimeUtils
import webscrapper.util.Utils._
import webscrapper._
import webscrapper.util.Logger

class Round(alive: List[Player], start: LocalDateTime, val tpe: RoundType, timeAfterStartGame: Int, val order: Int) extends Serializable with Logger {
  require(alive != null && alive.size > 0 && start != null && order >= 0 && timeAfterStartGame >= 0 && tpe != null)
  val startedAt = start
  private var _killedByMafia: Option[Player] = None
  private var _killedByManiac: Option[Player] = None
  private var _checked: Option[Player] = None
  private var _timeouted: ListBuffer[Player] = ListBuffer()
  private var _doctorTried: Option[Player] = None
  private var _doctorSaved: Option[Player] = None
  private var _moroz: Option[Player] = None
  private var _prisoned: Option[Player] = None
  private var _prisonedOmon: Option[Player] = None
  private var _votes: ListBuffer[Vote] = ListBuffer()
  private var _messages: ListBuffer[Message] = ListBuffer()
  private var _finish: LocalDateTime = _

  def votes = _votes
  def prisoner = _prisoned
  
  def killed = _killedByMafia ++ _killedByManiac
  def timeouts = _timeouted
  def wasOmon = _prisonedOmon.isDefined
  
  def playersOnOmon = {
    alive.filter(_moroz != Some(_))
  }
  def alivedPlayers = alive
  def finishedAt = _finish

  def duration = formatDuration(start, _finish)

  def complete(time: LocalDateTime) = {
    _finish = time
  }

  private def whoShouldCheck: Option[Player] = {
    require(typeIs(RoundType.KOMISSAR))
    val komissarAlive = !alived(_.isKomissar).isEmpty && !inMoroz(komissar)
    komissarAlive match {
      case true => komissar
      case false => inMoroz(serzhant) match {
        case false => serzhant
        case true => None
      }
    }
  }

  private def action(players: List[Option[Player]])(op:  => Unit) = {
    require(!players.isEmpty, "List of players is empty")
    require(players.size < 3, s"There should be 1 or 2 players, but found ${players.size}")
    for (player <- players) playerIsOk(player)
    if (players.size == 2) require(players.head != players.last, "Both players are equal and it is wrong")
    op
  }

  private def need(op: => Boolean, message: => String) = {
    require(op, message)
  }

  private def playerIsOk(player: Option[Player]) = {
    require(player.isDefined, "Player is absent but required")
    require(alived(player), s"This player is not alive ${player.get.name}")
  }

  def kill(killed: Player) = {
    if (alivePlayersCount(_.isMafia) > 0) {
      action(List(killed)) { 
        need(typeIs(RoundType.MAFIA), s"Round type should be RoundType.MAFIA, but it called on $tpe")
        val aliveMafia = alivePlayersCount(_.isMafia)
        val mafiaVoted = _votes.filter(_.toPlayerFromMafia(killed)).size
        val byManiac = _killedByMafia.isDefined || aliveMafia != mafiaVoted || _doctorSaved.isDefined
        if (byManiac) killByManiac(killed) else killByMafia(killed)
      }
    } else { log("Count of mafia players is 0 - maybe known bug in mafiaonline maybe not, check please") }
  }

  def killByMafia(killed: Player) = {
    action(List(killed)) { 
      need(typeIs(RoundType.MAFIA), s"Round type should be RoundType.MAFIA, but it called on $tpe")
      need(!_killedByMafia.isDefined, s"This round already contains player, killed by mafia ${_killedByMafia.get.name}")
      need(alivePlayersCount(_.isMafia) > 0, "Count of  alive players with role=Mafia is 0")
      need(killed.canBeKilledByMafia, s"Player role can't be Mafia or Child, but found ${killed.role}")
      _killedByMafia = killed
    }
  }

  def killByManiac(killed: Player) = {
    action(List(killed)) { 
      need(typeIs(RoundType.MAFIA), s"Round type should be RoundType.MAFIA, but it called on $tpe")
      need(!_killedByManiac.isDefined, s"This round already contains player, killed by maniac ${_killedByManiac.get.name}")
      need(alivePlayersCount(_.isManiac) > 0, "Count of  alive players with role=Maniac is 0")
      need(!killed.isManiac, s"Player role can't be Maniac")
      _killedByManiac = killed
    }
  }

  def countPlayers = alive.size

  def countMessages = _messages.size

  def addVote(vote: Vote) = {
    require(alived(vote.target) && alived(vote.destination))
    _votes += vote
  }

  def addMessage(message: Message) = {
    require(message != null)
    _messages += message
    message
  }

  def doctorSuccess = {
    _doctorSaved = _votes.find { _.fromRole(Role.DOCTOR) }.map(_.destination)
  }

  def check(komissar: Player, checked: Player) = {
    action(List(komissar, checked)) { 
      need(typeIs(RoundType.KOMISSAR), s"Round type should be RoundType.Komissar, but it called on $tpe")
      need(komissar.isKomissar, s"Player ${komissar.name} should have komissar role but it ${komissar.role}")
      need(!_checked.isDefined, s"Some player already checked( ${_checked.get.name})")
      need(!checked.isKomissar, s"Player ${checked.name} should not have komissar role but it ${checked.role}")
      _checked = checked
    }
  }

  def goPrison(prisoned: Player) = {
    action(List(prisoned)) { 
      need(typeIs(RoundType.CITIZEN), s"Round type should be RoundType.Citizen, but it called on $tpe")
      need(!_prisoned.isDefined , s"This round already have prisoner ${_prisoned.get.name}")
      _prisoned = prisoned
    }
  }
  
  def goPrisonOnOmon(prisoned: Player) = {
    action(List(prisoned)) { 
      need(typeIs(RoundType.CITIZEN), s"Round type should be RoundType.Citizen, but it called on $tpe")
      need(!_prisonedOmon.isDefined, s"This round already have prisoner ${_prisonedOmon.get.name}")
      _prisonedOmon = prisoned
    }
  }

  def doctorAttempt(doctor: Player, pacient: Player) = {
    action(List(doctor, pacient)) { 
      need(typeIs(RoundType.MAFIA), s"Round type should be RoundType.Mafia, but it called on $tpe")
      need(doctor.isDoctor, s"Player ${doctor.name} should have doctor role but it ${doctor.role}")
      need(!pacient.isDoctor, s"Player ${pacient.name} should not have doctor role but it ${pacient.role}")
      need(!_doctorTried.isDefined, s"This round already have person that tried to save by doctor ${_doctorTried.get.name}")
      _doctorTried = pacient
    }
  }

  def freeze(boss: Player, moroz: Player) = {
    action(List(boss, moroz)) { 
      //need(typeIs(RoundType.BOSS), s"Round type should be RoundType.Boss, but it called on $tpe")
      need(boss.isBoss, s"Player ${boss.name} should have boss role but it ${boss.role}")
      need(!moroz.isMafia, s"Player ${moroz.name} should not have mafia role but it ${moroz.role}")
      _moroz = moroz
    }
  }

  def missedCheck(player: Player) = typeIs(RoundType.KOMISSAR) && whoShouldCheck.isDefined && whoShouldCheck.get == player && !_checked.isDefined

  def doctorAttemptToMafia = typeIs(RoundType.MAFIA) && _doctorTried.map(_.isMafia).getOrElse(false)

  def doctorAttemptToImportantGoodRole = typeIs(RoundType.MAFIA) && _doctorTried.map(_.isImportantGoodRole).getOrElse(false)

  def checkedRole(player: Player) = typeIs(RoundType.KOMISSAR) && whoShouldCheck == Some(player) && _checked.map(_.isRole).getOrElse(false)

  def checkedMafia(player: Player) = typeIs(RoundType.KOMISSAR) && whoShouldCheck == Some(player) && _checked.map(_.isMafia).getOrElse(false)

  def hasFrozenRole = typeIs(RoundType.BOSS) && _moroz.map(_.isRole).getOrElse(false)

  def getFrozen = _moroz

  def maniacKilledRole = typeIs(RoundType.MAFIA) && _killedByManiac.map(_.isRole).getOrElse(false)

  def hasKilledByManiac = typeIs(RoundType.MAFIA) && _killedByManiac.isDefined
  
  def hasSavedByDoctor = typeIs(RoundType.MAFIA) && _doctorSaved.isDefined
  
  def hasCheck = typeIs(RoundType.KOMISSAR) && _checked.isDefined

  def countSmileMessages = _messages.filter(_.hasSmiles).size

  def countSmiles = _messages.map(_.countSmiles).sum

  def countMessagesFrom(player: Player) = _messages.filter(_.from(player)).size

  def countSmileMessagesFrom(player: Player) = _messages.filter(_.hasSmileFrom(player)).size

  def alived(rolePredicate: Player => Boolean) = alive.filter(rolePredicate)

  def aliveCitizenPlayers = alive.filter(x => x.isPositive && !x.isKomissar)

  def hasVoteFrom(player: Player) = _votes.filter(_.from(player)).size > 0 && alived(player)

  def countVotesFor(player: Player) = _votes.filter(_.to(player)).size

  def hasVoteToMafiaFrom(player: Player) = _votes.filter(_.toMafiaFrom(player)).size > 0

  def hasVoteToAnyRoleFrom(player: Player) = _votes.filter(_.fromPlayerToRole(player)).size > 0

  def countSmilesFrom(player: Player) = _messages.map(_.countSmilesFrom(player)).sum

  def hasVoteFromToRole(player: Player, role: Role) = _votes.filter(_.fromPlayerToSpecificRole(player, role)).size > 0

  def typeIs(roundType: RoundType) = tpe == roundType

  def inMoroz(player: Option[Player]) = equal(player, _moroz)

  def addTimeout(player: Player) = _timeouted += player

  def alived(player: Player) = alive contains player

  def alived(player: Option[Player]) = alive contains player.getOrElse(false)

  def isPrisoned(player: Player) = typeIs(RoundType.CITIZEN) && equal(player, _prisoned)

  def hasKillDuplet = typeIs(RoundType.MAFIA) && _killedByMafia.isDefined && _killedByManiac.isDefined && _killedByMafia == _killedByManiac

  def lastAlive(player: Player) = alive.size == 1 && alived(player)

  def wasKilled(player: Player) = typeIs(RoundType.MAFIA) && (killedByMafia(player) || killedByManiac(player))

  def killedByManiac(player: Player) = typeIs(RoundType.MAFIA) && equal(player, _killedByManiac)

  def killedByMafia(player: Player) = typeIs(RoundType.MAFIA) && equal(player, _killedByMafia)

  def checked(player: Player) = typeIs(RoundType.KOMISSAR) && equal(player, _checked)

  def wasTimeout(player: Player) = {
    require(player != null)
    _timeouted contains player
  }

  def hasMoroz = typeIs(RoundType.BOSS) && _moroz.isDefined

  def doctorAttempTo(player: Player) = typeIs(RoundType.MAFIA) && equal(player, _doctorTried)

  def smiles = _messages.map(_.smiles).flatten

  def messages = _messages

  def hasKills = typeIs(RoundType.MAFIA) && _killedByMafia.isDefined || _killedByManiac.isDefined

  def alivePlayersCount(predicate: Player => Boolean) = alive.filter(predicate).size

  def hasTimeout = _timeouted.size > 0

  def smileMessages = _messages.map(_.smiles).flatten.toList

  def hasPrisoners = typeIs(RoundType.CITIZEN) && _prisoned.isDefined

  def hasChecks = typeIs(RoundType.KOMISSAR) 
  
  def hasManiac = typeIs(RoundType.MAFIA) && alived(maniac)
  
  def hasDoctor = typeIs(RoundType.MAFIA) && alived(doctor)
  
  def hasKomissar = typeIs(RoundType.KOMISSAR) && alived(komissar)

  def hasSerzhant = typeIs(RoundType.KOMISSAR) && alived(serzhant)
  
  def hasDoctorSaved = typeIs(RoundType.MAFIA) && _doctorSaved.isDefined

  def hasDoctorAttempt = typeIs(RoundType.MAFIA) && _doctorTried.isDefined

  def hasMissedMoroz = typeIs(RoundType.BOSS) && !_moroz.isDefined && alived(boss)

  def doctorMissedVote = typeIs(RoundType.MAFIA) && !_doctorTried.isDefined && !inMoroz(doctor) && alived(doctor)

  def maniacMissedKills = typeIs(RoundType.MAFIA) && !_killedByManiac.isDefined && !inMoroz(maniac) && alived(maniac) && _killedByMafia != maniac

  def wasAliveIn(roundType: RoundType, player: Player) = typeIs(roundType) && alived(player)

  def wasNotPrisoned(player: Player) = typeIs(RoundType.CITIZEN) && hasPrisoners && !inMoroz(player) && alived(player)

  def rolesOrder(rolePredicate: Player => Boolean) = {
    val players = alived(rolePredicate)
    for (player <- players) yield (alive.indexOf(player) + 1).toString
  }

  def votesOrder(rolePredicate: Player => Boolean) = {
    val players = alived(rolePredicate)
    for (player <- players) yield hasVoteFrom(player) match {
      case false => "Не голосовал"
      case true => voteIndex(player).toString
    }
  }

  def mafiaVotesOrder = votesOrder(_.isMafia)

  def citizenVotesOrder = votesOrder(_.isCitizen)

  def voteIndex(player: Option[Player]) = {
    val index = for (vote <- _votes if player.isDefined && (vote.from(player.get))) yield _votes.indexOf(vote) + 1
    if (index != null && index.size > 0) index(0) else -1
  }

  def komissarVotesOrder = votesOrder(_.isKomissar)
  
  def bossVotesOrder = votesOrder(_.isBoss)

  def countVotes = _votes.size

  def checkedPlayer = _checked

  def killedByMafiaPlayer = _killedByMafia
  
  def killedByManiacPlayerRole = _killedByManiac.map {_.roleName}
  
  def doctorSavedPlayerRole = _doctorSaved.map {_.role}
  
  def doctorSavedPlayerRoleRaw  = _doctorSaved.map {_.roleName}
  
  def doctorAttemptedPlayerRole = _doctorTried.map {_.roleName}
  
  def doctorAttemptedPlayer = _doctorTried
  
  def komissarCheckRole = _checked.map {_.roleName}
  
  def bossFreezeRole = _moroz.map {_.roleName}

  def checkedRole = _checked.map(_.roleName).getOrElse("Комиссар посажен на первом ходу")

  def killedMafia = _killedByMafia
  
  def killedManiac = _killedByManiac

  def checkMafiaProbability = {
    if (alivePlayersCount(_.isKomissar) > 0) alivePlayersCount(_.isMafia).toDouble / (alive.size - alivePlayersCount(_.isKomissar)) else -1.0
  }

  def killKomissarProbability = {
    if (alivePlayersCount(_.isKomissar) > 0) alivePlayersCount(_.isKomissar).toDouble / (alive.size - alivePlayersCount(_.isMafia) - alivePlayersCount(_.isChild)) else -1.0
  }

  private def doctor = first(alived(_.isDoctor))

  private def maniac = first(alived(_.isManiac))

  private def komissar = first(alived(_.isKomissar))

  private def serzhant = first(alived(_.isSerzhant))

  private def boss = first(alived(_.isBoss))

  def prisonerRole = _prisoned.map(_.roleName).getOrElse("Не было посадок")
  
  def prisonerName = _prisoned.map(_.name).get
  
  def morozRole = _moroz.map(_.role)
  
  def morozName = _moroz.map(_.name)

  def firstPrisonedMafiaProbability = {
    alivePlayersCount(_.isMafia).toDouble / (alive.size)
  }

  def firstVoteTo: Option[String] = {
    _votes.size match {
      case 0 => None
      case x: Int => Some(_votes(0).destination.roleName)
    }
  }

  def firstNotMafiaVoteTo: Option[String] = _votes.find(!_.fromMafia).map(_.destination.roleName)

  def messageNumberXFrom(number: Int, player: Player) = {
    val messages = _messages.filter(_.from(player))
    if (messages.size < number) None else Some(messages(number - 1))
  }

  def messageNumberXTime(number: Int, player: Player) = {
    messageNumberXFrom(number, player).map(_.timeFromStart - timeAfterStartGame)
  }

  def diffBetweenFirstAndSecondMessages(player: Player) = {
    val first = messageNumberXFrom(1, player)
    val second = messageNumberXFrom(2, player)
    if (first.isDefined && second.isDefined) new Some(second.get.timeFromStart - first.get.timeFromStart) else None
  }

  def firstMessageFrom(player: Player) = messageNumberXFrom(1, player)

  def firstMessageTime(player: Player) = messageNumberXTime(1, player)
  
  def hasOtvetka(player:Player) = {
    val votesTo = _votes.filter(_.to(player))
    val from = _votes.filter(_.from(player))(0)
    val toPlayer = from.destination
    val vote = votesTo.filter(_.target == toPlayer).toList
    vote match {
      case Nil => false
      case _ => {
        val f =_votes.indexOf(from) 
        val v =_votes.indexOf(vote(0))
        f > v
      }
    }
  }

}