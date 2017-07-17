package webscrapper

import scala.collection.mutable.ListBuffer
import java.time.LocalDateTime
import webscrapper.parser.TimeoutEvent
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import webscrapper.RoundType._
import webscrapper.parser.Event.handlers
import webscrapper.util.Utils._
import webscrapper.Result.MAFIA_WIN
import webscrapper.FinishStatus._
import webscrapper.util.TextUtil.StringImprovements
import webscrapper.util.TimeUtils._
import scala.collection.immutable.ListMap
import java.time.format.DateTimeFormatter
import scala.language.postfixOps
import webscrapper.util.Logger

case class ChatMessage(text:String, time:String, from:String, avatar:String, system:Boolean, invisible:Boolean)

case class ChatVote(index:Int, descr:String, votes:List[Vote])

class GameSimulation(val id: Long, val location: Location, val start: LocalDateTime, val players: List[Player], actions: List[(String, Int)]) extends Logger {

  def maxMessageIndex(order:Int) = {
    val message = chat.filter(_.text == "" +  order).find(_.invisible).get
    chat.indexOf(message)
  }
  
  def roundsList = 1 to rounds.size toList
  
  val roleMap = new collection.mutable.HashMap[Role, List[(String, String)]]
  
  roleMap.put(Role.CITIZEN, List(("F" -> "https://st.mafiaonline.ru/images/roles/lolw.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/lol.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/chizhu.gif")))
  roleMap.put(Role.MAFIA, List(("F" -> "https://st.mafiaonline.ru/images/roles/mafiaw.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/mafia.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/mafu.gif")))
  roleMap.put(Role.BOSS, List(("F" -> "https://st.mafiaonline.ru/images/roles/sutener_w.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/sutener_m.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/bossu.gif")))
  roleMap.put(Role.DOCTOR, List(("F" -> "https://st.mafiaonline.ru/images/roles/doktor_w.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/doktor_m.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/doku.gif")))
  roleMap.put(Role.MANIAC, List(("F" -> "https://st.mafiaonline.ru/images/roles/maniak_w.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/maniak_m.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/manu.gif")))
  roleMap.put(Role.SERZHANT, List(("F" -> "https://st.mafiaonline.ru/images/roles/serj_w.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/serj_m.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/serzhu.gif")))
  roleMap.put(Role.KOMISSAR, List(("F" -> "https://st.mafiaonline.ru/images/roles/komissarw.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/komissar.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/komu.gif")))
  roleMap.put(Role.CHILD, List(("F" -> "https://st.mafiaonline.ru/images/roles/boss.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/boss.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/boss.jpg")))
  roleMap.put(Role.CHILD_GIRL, List(("F" -> "https://st.mafiaonline.ru/images/roles/bossw.jpg"), ("M" -> "https://st.mafiaonline.ru/images/roles/bossw.jpg"), ("N" -> "https://st.mafiaonline.ru/images/roles/bossw.jpg")))
  roleMap.put(Role.CHILD_UNKNOWN, List(("F" -> "https://st.mafiaonline.ru/images/roles/vsbu.gif"), ("M" -> "https://st.mafiaonline.ru/images/roles/vsbu.gif"), ("N" -> "https://st.mafiaonline.ru/images/roles/vsbu.gif")))
  def roleAvatar(player:Player) = {
    roleMap.get(player.role).get.filter(_._1 == player.sex).map(_._2).apply(0)
  }
  
  def status(player:Player, order:Int) = {
    if (prisoners(order).contains(player)) "В тюрьме" else if(killed(order).contains(player)) "Убит" else if(timeouted(order).contains(player)) "Тайм" else "В игре"
  }
  
  def off(order:Int) = {
    prisoners(order) ++ killed(order) ++ timeouted(order)
  }
  
  def vote(order:Int) = {
    citVotes(order).map(a => ChatVote(a._1, "Ход номер " + a._1, a._2))
  }
  private val _alivePlayers: ListBuffer[Player] = players.to[ListBuffer]
  private var _currentlyFrozen: Option[Player] = None
  private var _lastActivityTime = start
  val rounds = ListBuffer[Round](new Round(players, start, INITIAL, 0, 0))
  private var _result: Option[Result] = None
  private var _finish: Option[LocalDateTime] = None
  private var _omon: Option[OmonStatus] = None
  private val roundActions = collection.mutable.HashMap[Int, ListBuffer[(String, Int)]]()
  
  def finished = _finish.isDefined

  def run = {
    //actions.foreach(println)
    actions.foreach(act => {
      //println(act._1)
      findHandler(act._1, this).apply(act, this)
      val list = roundActions.getOrElse(currentRound.order, new ListBuffer[(String, Int)]()) += act
      roundActions.update(currentRound.order, list)
    })
    fixMorozStatus
    Option(toGame)
  }
  
  def runTo(order:Int) = {
    actions.foreach(act => {
        findHandler(act._1, this).apply(act, this)
      val list = roundActions.getOrElse(currentRound.order, new ListBuffer[(String, Int)]()) += act
      roundActions.update(currentRound.order, list)
    })
    Option(this)
  }

  private def findHandler(action: String, game: GameSimulation) = handlers.find(_.isHappened(action, game)).getOrElse(throw new IllegalArgumentException("Can't find handler for $action"))

  private def fixMorozStatus = {
    if (_result.get == MAFIA_WIN) _alivePlayers.filter(!_.isMafia).foreach(_.status = MOROZ)
  }

  /**
   * Find player by name
   * @param name
   * @return player
   */
  def findPlayer(name: String) = {
    require(name != null && !name.isEmpty)
    players.find(_.name.trim.toLowerCase == name.trim.toLowerCase)
  }

  private def currentRound = rounds(rounds.size - 1)

  def omonCanHappen = (_alivePlayers.filter(!_.isMafia).size == _alivePlayers.filter(_.isMafia).size + (if (_currentlyFrozen.isDefined) 1 else 0)) || (_omon.isDefined && _alivePlayers.filter(!_.isMafia).size >= _alivePlayers.filter(_.isMafia).size)
  /**
   * Method called when parser determined that some player leave the game by timeout
   */
  def timeout(player: Player) = {
    require(_alivePlayers.contains(player))
    _alivePlayers -= player
    currentRound.addTimeout(player)
    resetFrozen
  }

  /**
   * Finish current round and add new one if game is not over
   * @param text - parsed line
   * @param nextRound - next round or null if game is over
   */
  def finishRound(action: (String, Int), nextRoundType: Option[RoundType]) = {
    val time = action._2
    val text = action._1
    _lastActivityTime = addTime(timeDiff(time), currentRound.startedAt)
    currentRound.complete(_lastActivityTime)
    if (nextRoundType.isDefined) {
      addRound(new Round(_alivePlayers.toList, currentRound.finishedAt, nextRoundType.get, time, currentRound.order + 1));
    }
  }

  /**
   * Adds new round to the game.
   * At first, retrieve previous round order,
   * increment it and assign it to new round order.
   * @param round
   */
  private[webscrapper] def addRound(round: Round) = {
    rounds += round
    resetFrozen
  }

  /**
   * Adds vote to the current round
   * @param vote
   */
  def addVote(vote: Vote) = currentRound addVote vote

  /**
   * Method called when parser determined that doctor successfully saved some player
   */
  def doctorSaved = currentRound doctorSuccess

  /**
   * Method called when parser determined that doctor tried to save some player
   * @param who - doctor's name
   * @param whom - player that was saved by the doctor
   */
  def doctorAttempt(target: Player, destination: Player) = {
    act(target, destination, currentRound.doctorAttempt)
  }

  private def act(target: Option[Player], destination: Option[Player], op: (Player, Player) => Unit = (a, b) => {}) = {
    for {
      t <- target
      d <- destination
      o = op(t, d)
      v = Vote(t, d)
      r = addVote(v)
    } yield resetFrozen
  }

  /**
   * Finishes game
   * @param result of game
   */
  def finish(result: Result) = {
    require(!_result.isDefined && !_finish.isDefined)
    _result = Option(result)
    _finish = Option(currentRound.finishedAt)
  }

  /**
   * Method called when parser determined that player was killed at the night
   * @param killed
   */
  def kill(player: Player) = {
    currentRound.kill(player)
    _alivePlayers -= player

  }

  /**
   * Method called when parser determined that sheriff checked role of some player
   * @param who - sheriff name
   * @param whom - name of checked player
   */
  def check(target: Player, destination: Player) = {
    require(!alreadyChecked(destination), s"Player ${destination.name} already checked by komissar")
    act(target, destination, currentRound.check)
  }

  private def alreadyChecked(player: Player) = rounds.find(_.checkedPlayer == player).isDefined

  /**
   * Method called when parser determined that player with role mafia
   * tried to kill some player
   * @param who - mafia player name
   * @param whom - who was killed
   */
  def mafiaVoted(target: Player, destination: Player) = act(target, destination)

  /**
   * Method called when parser determined that maniac killed someone
   * @param who -maniac's name
   * @param whom - name of killed player
   */
  def maniacKill(target: Player, destination: Player) = act(target, destination)

  /**
   * Method called when parser determined that boss frozen some player
   * @param who - name of boss
   * @param whom - name of frozen player
   */
  def freeze(target: Player, destination: Player) = {
    act(target, destination, currentRound.freeze)
    _currentlyFrozen = Option(destination)
  }

  def omonHappened = {
    if (!_omon.isDefined) _omon = Option(OmonStatus.byCount(_alivePlayers.filter(_.isMafia).size))
  }

  /**
   * Method called when parser determined that player went to prison
   * @param prisoned
   */
  def goPrizon(player: Player) = {
    currentRound.goPrison(player)
    _alivePlayers -= player
  }

  def goPrizonOnOmon(player: Player) = {
    currentRound.goPrisonOnOmon(player)
    omonHappened
    _alivePlayers -= player
  }

  /**
   * @param player
   * This method called, when some player in frozen state
   */
  def resetFrozen = {
    _currentlyFrozen = for {
      boss <- first(findPlayersByRole(Role.BOSS))
      currentMoroz <- _currentlyFrozen
      if (_alivePlayers contains currentMoroz) 
      o = currentRound.freeze(boss, currentMoroz)
      frozen <- currentRound.getFrozen
    } yield frozen

  }

  /**
   * Find player by name
   * @param name
   * @return player
   */
  private def findPlayersByRole(role: Role) = {
    require(role != null)
    _alivePlayers.filter(_.isInRole(role)).toList
  }

  /**
   * Adds message to the current round
   * @param message
   */
  def addMessage(message: Message) = currentRound addMessage message

  def toGame = new Game(players, id, location, start, _finish.get, rounds.toList, _result.get, _omon)

  def makeMessage(action: (String, Int)) = {
    try {
      val text = action._1.erase("<font class=\"chat_text\">", "</font>", "<span class=\"chat_text\">", "</span>")
      val smiles = text.findAll("""http([^\s-]+)gif""")
      val who = findPlayer(text.between("[", "]"))
      //println(text)
      if (who.isDefined) {
        val content = text.erase("\\[", who.get.name, "\\]")
        addMessage(new Message(who.get, content, smiles, action._2))
      } else if (action._2 == 0) {
        println("Message before game started " + action._1)
      } else {
        throw new Exception()
      }
      
    } catch {
      case e: Exception => e.printStackTrace; log("Failed message:" + action._1); throw e
    }
  }
  
  def prisoners(order:Int) = rounds.filter(_.order <= order).flatMap(_.prisoner)
  
  def killed (order:Int) = rounds.filter(_.order <= order).flatMap(_.killed)
  
  def timeouted (order:Int) = rounds.filter(_.order <= order).flatMap(_.timeouts)
  
  def alived(order:Int) = rounds.find(_.order == order).get.alivedPlayers
  
  def chat = {
    val map = ListMap(roundActions.toSeq.sortBy(_._1):_*)
    map.flatMap(f => {
      val messages = f._2
      messages.map(toChatMessage) += ChatMessage("" + f._1, "", "", "",true, true)
    }).filter(!_.text.contains("<b style=\"text-decoration: underline; color:")).toList
  }
  
  def toChatMessage(act:(String, Int)) = {
    val mess = act._1
    val time = act._2
    val addon = start.plusSeconds(time).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    val replaced = mess.replaceAll("<font class=\"chat_text\">", "").replaceAll("</font>", "").replaceAll("<b style=\"text-decoration: underline;\">", "").replaceAll("<img ", "<img style=\"width:auto;height:auto\" ").replaceAll("<span class=\"chat_text\">", "")
    val system = replaced.startsWith("[ОМОНОВЕЦ]")
    val a = addon + replaced
    val who = findPlayer(replaced.between("[", "]"))
    val name = if(who.isDefined) who.get.name else "ОМОНОВЕЦ"
    val avatar = if(who.isDefined) who.get.avatar else "https://st.mafiaonline.ru/images/avatars/nophoto_u.png"
      val content = if(who.isDefined) replaced.erase("\\[", name, "\\]") else replaced
    ChatMessage(content, addon, name, avatar,system, false)
  }

  def citVotes(order:Int) = {
    rounds.filter(_.tpe == RoundType.CITIZEN).filter(_.order <= order).map(a => (a.order + 1 -> a.votes.toList)).toList
  }
  
}