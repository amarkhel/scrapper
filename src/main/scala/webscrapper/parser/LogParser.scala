package webscrapper.parser

import org.jsoup.nodes.Document
import webscrapper.Player
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import webscrapper.Location
import org.jsoup.nodes.Element
import webscrapper.FinishStatus
import org.jsoup.select.Elements
import org.jsoup.nodes.Node
import scala.collection.JavaConversions._
import webscrapper.GameSimulation
import webscrapper.GameSource
import scala.util.Try
import org.springframework.web.util.UriUtils
import scala.util.Success
import scala.util.Failure
import webscrapper.GameSimulation
import webscrapper.Role
import webscrapper.util.TextUtil._
import webscrapper.util.TimeUtils._
import webscrapper.database.DB
import webscrapper.util.Logger

object LogParser extends Logger {
  
  val LOG_NOT_FOUND = "Лог партии с данным номером не найден."
  
  def parse(doc: Document, needFetchInfo:Boolean = false) : Option[GameSimulation] = {
    implicit val document = doc
    implicit val gamers = grabPlayers
    val simulation = for {
      _ <- 1 to 1
      gameCaption <- grabCaption
      id = grabId(gameCaption)
      if checkLogExist(id)
      location = grabLocation(gameCaption)
      start = grabStartTime(gameCaption)
      players = fetchUsers(needFetchInfo)
      actions = filterActions
    } yield new GameSimulation(id, location, start, players, actions)
    Option(simulation.head)
  }
  
  def simulate(doc: Document, order:Int=(-1)) = parse(doc, true)
  
  private def fetchUsers(needFetch : Boolean)(implicit gamers:List[Player]) = {
    needFetch match {
      case true => gamers.par.map(fetchUserInfo).toList
      case false => gamers
    }
  }
  
  def fetchUserInfo(player:Player) = {
    val doc = GameSource.fromUrl(s"https://www.mafiaonline.ru/info/${UriUtils.encodeQuery(player.name, "UTF-8")}")
    val (avatar, sex) = doc match {
      case Some(d) => {
        implicit val document = d
        (grabAvatar, grabSex)
      }
      case None => {
        //e.printStackTrace() User in passport
        ("http://172.110.9.65:8080/viewer/img/omon.jpg", "N")
      }
    }
    player.avatar(avatar)
    player.sex(sex)
    player
  }  
  
  private def filterActions(implicit document: Document, gamers: List[Player]) = {
    val chat = messages.mkString(" ").replaceAll("<br>", "&br&").replaceAll("&nbsp;", " ")
    //println(chat)
    val mess = chat.split("&br&").map(_.trim).filter(!_.isEmpty).toList
    val actionPairs = mess.map(_.splitAt(6))
    val startTime = actionPairs.head._1.trim
    var lastTime = 0
    val actions = actionPairs.map(a => {
      var act = (a._2, extractTimePattern(a._1.trim, startTime, lastTime))
      if (act._2 == -1) {
        log("Почему-то нет метки времени" + a._1 + a._2)
        //В случае если кто-то шепнул нескольким, то второе сообщение о шепоте не будет иметь метки времени, поэтому берем предыдущую
        act = (a._1 + a._2, lastTime)
      }
      lastTime = act._2
      act
    })
    //actions.foreach(println)
    val filtered = actions.filter(a => a._1.nonEmpty && a._2 >= 0)
    filtered.span(elem => !GameFinishedEvent.isHappened(elem._1, null)) match {case (h, t) => h ++ t.take(1)}
  }
  
  private def grabAvatar(implicit document: Document) = document.select(".avatar").attr("src")
  
  private def grabSex(implicit document:Document) = {
    val female = document.select("img[alt*=жен.]")
    if (female.size() > 0) "F" else "M"
  }

  private def checkLogExist(id:Long)(implicit document: Document): Boolean = {
    if(messages.size == 1 && messages.head.toString.contains(LOG_NOT_FOUND)) {
      DB().addInvalid(id, LOG_NOT_FOUND); false
    } else true
  }
  
  private def messages(implicit doc:Document) = doc.select(".log_text").head.childNodes
  
  private def grabStartTime(gameCaption: String) = format(gameCaption.substring(gameCaption.indexOf("(")).between(",", ")"))

  private def grabCaption(implicit document: Document) :Option[String] = Option(document.select(".players .hglghtd").text)
  
  private def grabPlayers(implicit document: Document) = document.select(".players").first.select("tr").flatMap(grabPlayer).toList

  private def grabPlayer(elem: Element) = {
    val player = for {
      _ <- 1 to 1 // synthetic value to run at least 1 time
      images = elem.select("img")
      first = images.first
      last = images.last
      finishStatus = FinishStatus.map(first.attr("alt"))
      role = Role.get(last.attr("alt"))
      name = grabPlayerName(elem)
    } yield new Player(role, finishStatus, name)
    player.headOption
  }

  private def grabPlayerName(elem: Element) = elem.select("span").head.text.replace("\u00A0"," ")
  
  private def grabId(caption: String) = caption.between("№", " ").toLong

  private def grabLocation(caption: String) = Location.get(caption.between("(", ","))

}