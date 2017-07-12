package webscrapper.parser

import webscrapper.GameSimulation
import webscrapper.RoundType
import webscrapper.Vote
import webscrapper.Vote
import webscrapper.Result
import webscrapper.Player
import webscrapper.util.TextUtil._
import org.jsoup.select.Evaluator.IsEmpty
import webscrapper.service.StoppedException
import scala.util.matching.Regex

sealed trait Event {
  def isHappened(implicit action: String, game: GameSimulation): Boolean = canApply || condition(action)
  def apply(implicit action: (String,Int), game: GameSimulation): Unit
  protected def condition(action: String): Boolean = false
  protected def patterns: List[String] = List()
  protected def regex: Option[Regex] = None
  protected def isMessage(implicit action: String) = action.contains("<font class=\"chat_text\">")
  protected def specialCondition(implicit action: String, game: GameSimulation) = true
  private def canApply(implicit action: String, game: GameSimulation) = !isMessage && specialCondition(action, game) && (patterns.find(action contains _).size > 0 || (regex.isDefined && applyRegex(action, regex.get).size > 0))
  protected def applyRegex(text: String, regex: Regex) = {
    text match {
      case regex(player1, player2) => cleanName(player1, player2)
      case regex(player1) => cleanName(player1)
      case _ => List.empty
    }
  }
  
  private def cleanName(players:String*) = players.map(_.trim.replace("&nbsp;", " ").replace("\u00A0"," "))
}

sealed trait FinishRoundEvent extends Event {
  protected def roundType: Option[RoundType] = None
  override def apply(implicit action: (String,Int), game: GameSimulation) = game.finishRound(action, roundType)
}

sealed trait ActionEvent extends Event {
  
  private def findPlayers(implicit text: String, game: GameSimulation) = {
    val replaced = text.trim
    applyRegex(replaced, regex.get).flatMap(game.findPlayer).toList
  }
  
  override def apply(implicit action: (String,Int), game: GameSimulation) = {
    val players = findPlayers(action._1, game)
    if (players.size == countPlayers) applyAction(players, game) else throw new IllegalArgumentException(s"Required $countPlayers for this action, but found ${players.size}")
  }
  protected def applyAction(implicit players: List[Player], game: GameSimulation): Unit
  protected def countPlayers = 2
}

case object TimeoutEvent extends ActionEvent {
  override def regex = Some("""\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline;\">Игрок (.+) вышел из партии по таймауту.</b>""".r)
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.timeout(players.head)
  override def countPlayers = 1
}

case object BossRoundStartedEvent extends FinishRoundEvent {
  override def patterns = List("Ход босса")
  override def roundType = Some(RoundType.BOSS)
}

case object CitizenRoundStartedEvent extends FinishRoundEvent {
  override def patterns = List("Наступил день", "Так как честные не смогли договориться, дадим им ещё попытку.")
  override def roundType = Some(RoundType.CITIZEN)
}

case object CitizenRoundVoteEvent extends ActionEvent {
  override def regex = Some("""\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline;\">(.+) xочет отправить в тюрьму (.+)</b>""".r)
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.addVote(new Vote(players.head, players.last))
}

case object DoctorSavedEvent extends Event {
  override def patterns = List("врач успел вовремя и спас игрока от смертельных ран.")
  override def apply(implicit action: (String,Int), game: GameSimulation) = game.doctorSaved
}

case object DoctorVotedEvent extends ActionEvent {
  override def regex = Some("""\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline; color: lightgreen;\">Врач (.+) пытается вылечить (.+).</b>""".r)
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.doctorAttempt(players.head, players.last)
}

case object GameFinishedEvent extends FinishRoundEvent {
  override def patterns = List("Игра окончена")
  override def apply(implicit action: (String,Int), game: GameSimulation) = {
    super.apply(action, game)
    action._1 match {
      case text if text.contains("Мафия победила") => game.finish(Result.MAFIA_WIN)
      //case text if text.contains("Ничья") => game.finish(Result.DRAW)
      case _ => game.finish(Result.GOROD_WIN)
    }
  }
}

case object SkipKillEvent extends ActionEvent {
  override def applyAction(implicit players: List[Player], game: GameSimulation) = ()
  override def regex = Some("""\[ОМОНОВЕЦ\] [\s\S]*  <b> (?:убит|убита|убит\(а\))</b>""".r)
  override def countPlayers = 0
  override def specialCondition(implicit action: String, game: GameSimulation) = regex.get.findFirstIn(action).isDefined
}

case object KillEvent extends ActionEvent {
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.kill(players(0))
  override def regex = Some("""\[ОМОНОВЕЦ\] [\s\S]*  <b>(.+)(?:убит|убита|убит\(а\))</b>""".r)
  override def countPlayers = 1
}

case object KomissarCheckedEvent extends ActionEvent {
  override def regex = Some("""\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline; color: #d5c32d;\">Комиссар (.+) проверил жителя (.+).</b>""".r)
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.check(players(0), players(1))
}

case object KomissarRoundStartedEvent extends FinishRoundEvent {
  override def patterns = List("Ход комиссара")
  override def roundType = Some(RoundType.KOMISSAR)
}

case object MafiaRoundStartedEvent extends FinishRoundEvent {
  override def patterns = List("Ход мафии")
  override def roundType = Some(RoundType.MAFIA)
}

case object MafiaVotedEvent extends ActionEvent {
  override def regex = Some("""\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline; color: red;\">Мафиози (.+) пытается убить (.+).</b>""".r)
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.mafiaVoted(players.head, players.last)
}

case object ManiacVotedEvent extends ActionEvent {
  override def regex = Some("""\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline; color: cyan;\">Маньяк (.+) убивает (.+).</b>""".r)
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.maniacKill(players.head, players.last)
}

case object MorozEvent extends ActionEvent {
  override def regex = Some("""\[ОМОНОВЕЦ\]  <b style=\"text-decoration: underline; color: #990000;\">Босс (.+) морозит (.+).</b>""".r)
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.freeze(players.head, players.last)
}

/*case object OmonEvent extends Event {
  override def patterns = List("Рассвирепевший ОМОНОВЕЦ, не разбираясь, кто прав, кто виноват, решил, что")
  override def apply(implicit action: (String,Int), game: GameSimulation) = game.omonHappened
}*/

case object PlayerPrisonedEvent extends ActionEvent {
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.goPrizon(players.head)
  override def regex = Some("""\[ОМОНОВЕЦ\] [\s\S]*  <b>(.+) (?:отправлен в тюрьму|отправлена в тюрьму|отправлен\(а\) в тюрьму).</b>""".r)
  override def countPlayers = 1
}

case object OmonPrisonedEvent extends ActionEvent {
  override def applyAction(implicit players: List[Player], game: GameSimulation) = game.goPrizonOnOmon(players.head)
  override def regex = Some("""\[ОМОНОВЕЦ\][\s\S]*Рассвирепевший ОМОНОВЕЦ, не разбираясь, кто прав, кто виноват[\s\S]*  <b>(.+) (?:будет отправлен в тюрьму|будет отправлена в тюрьму|будет отправлен\(а\) в тюрьму).</b>""".r)
  override def countPlayers = 1

}

case object MessageEvent extends Event {
  override def condition(text: String) = isMessage(text)
  override def apply(implicit action: (String,Int), game: GameSimulation) = game.makeMessage(action)
}

case object StoppedEvent extends Event {
  override def condition(text: String) = !isMessage(text) && text.contains("остановил партию")
  override def apply(implicit action: (String,Int), game: GameSimulation) = throw new StoppedException(action._1)
}

case object MissedHandlerEvent extends Event {
  override def condition(text: String) = true
  override def apply(implicit action: (String,Int), game: GameSimulation) = println("Missed handler for " + action._1 + " game id=" + game.id )
}

case object SkipEvent extends Event {
  override def patterns = List("[ОМОНОВЕЦ] Минуточку, распределяем роли.", "[ОМОНОВЕЦ] Дадим время договориться мафии. Приват включен!!!",
      "[ОМОНОВЕЦ] Игра началась!!! В игре участвуют:", "[ОМОНОВЕЦ] Внимание! Сейчас будет следующий ход.", "[ОМОНОВЕЦ] Считаем трупы!!! Результат хода честных людей.",
      "[ОМОНОВЕЦ] Считаем трупы! Результаты ночных беспорядков.", "[ОМОНОВЕЦ] Договориться не смогли.", "[ОМОНОВЕЦ]  <b>Сержант получает повышение. Мои поздравления, господин комиссар.</b>",
      "[ОМОНОВЕЦ] Маньяк проспал свой ход", "Воспользовался правом выйти из партии.</b>", "что-то шепнул", "<b> убит")

  override def apply(implicit action: (String,Int), game: GameSimulation) = ()
}

object Event {
  
  def handlers = List(SkipEvent,  TimeoutEvent, BossRoundStartedEvent, CitizenRoundStartedEvent, CitizenRoundVoteEvent, DoctorSavedEvent,
      DoctorVotedEvent, GameFinishedEvent, OmonPrisonedEvent, SkipKillEvent, KillEvent, KomissarCheckedEvent, KomissarRoundStartedEvent,
      MafiaRoundStartedEvent, MafiaVotedEvent, ManiacVotedEvent, MorozEvent,   PlayerPrisonedEvent, StoppedEvent, MessageEvent, MissedHandlerEvent)
}

