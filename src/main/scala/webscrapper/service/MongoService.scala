package webscrapper.service

import com.amarkhel.LocalDateTimeXMLConverter
import com.thoughtworks.xstream.XStream
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.MongoClient
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Accumulators._
import com.amarkhel.Helpers._
import webscrapper.Game
import org.mongodb.scala._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Indexes._
import com.mongodb.client.model.IndexOptions
import org.mongodb.scala.model.Indexes
import webscrapper.util.Logger

object MongoService extends Logger {

  /*def delete = {
    for (id <- "2933173,3531736,3531738,3678092".split(",")) 
      yield invalidGames.deleteOne(equal("gameId", id.toInt)).printHeadResult("Delete Result: ")

  }
  
  def loadAllInvalid = {
    invalidGames.find(notEqual("gameId", "1")).results().map(game => {
      game.get("gameId").get.asInt64.getValue -> game.get("reason").get.asString.getValue
    }).groupBy(_._2).map{case (k,v) => k -> v.size}.toList.sortBy(_._2)
  }
  
  def loadAllInvalid2 = {
    invalidGames.find(notEqual("gameId", "1")).results().map(game => {
      game.get("gameId").get.asInt64.getValue -> game.get("reason").get.asString.getValue
    }).filter(_._2 == "Глюк мафии").map(_._1).sortBy(identity).mkString(",")
  }*/
  def groupResults = {
    val proj = games.find(in("year", 2015,2017,2016)).projection(include("playersSize", "result")).results.map(e=> e.get("playersSize").get.asInt32().getValue -> e.get("result").get.asString.getValue)
    val agg = proj.groupBy(_._1)
    val res = agg.map(e => (e._1 -> e._2.map(_._2).groupBy(identity).map{case (k,v) => k -> v.size}))
    println(res)
    val perc = res.filter(_._2.size == 2).map{case (k,v) => k -> (v.get("Победа города").get.toDouble / (v.get("Победа города").get.toDouble + v.get("Победа мафии").get.toDouble))}
    println(perc)
   /* println(games.aggregate(List(filter(equal("year", 2015)),
                                 project(fields(include("playersSize", "result"), excludeId())),
                                 group("$playersSize"),
                                 group("$result"),
                                 out("result"))).results())*/
    //println(games.find(equal("playersSize", 11)).results())
  }
  
  def showIndexes = {
    val indexes = games.listIndexes.results()
    indexes.foreach(println)
    /*val a = new IndexOptions()
    games.createIndex(Document("gameId" -> 1), a.unique(true)).results()
    games.createIndex(ascending("playersSize")).results()
    games.createIndex(Indexes.text("players")).results()
    games.createIndex(ascending("location")).results()
    games.createIndex(ascending("rounds")).results()
    games.createIndex(ascending("result")).results()
    games.createIndex(ascending("tournamentResult")).results()
    games.createIndex(ascending("year")).results()
    games.createIndex(ascending("month")).results()
    games.createIndex(ascending("day")).results()
    val indexes2 = games.listIndexes.results()
    indexes2.foreach(println)
    val indexesInv = invalidGames.listIndexes.results()
    indexesInv.foreach(println)*/
    val a = new IndexOptions()
    invalidGames.createIndex(ascending("gameId")).results()
    val indexesInv2 = invalidGames.listIndexes.results()
    indexesInv2.foreach(println)
  }
  def addInvalid(id: Long, reason: String) = {
    val doc: Document = Document("gameId" -> id,
      "reason" -> reason)
    invalidGames.insertOne(doc).results()
    log(s"Game ${id} is invalid for $reason and saved to database")
  }

  def isInvalid(id: Long) = {
    val invalid = !invalidGames.find(equal("gameId", id)).first().results.isEmpty
    if (invalid) log(s"Game ${id} is invalid ")
    invalid
  }

  def save(game: Game) = {
    val xml = xstream.toXML(game)
    val doc: Document = Document("gameId" -> game.id,
      "playersSize" -> game.countPlayers,
      "players" -> game.players.map(_.name).mkString(","),
      "location" -> game.location.name,
      "rounds" -> game.countRounds,
      "result" -> game.result.descr,
      "tournamentResult" -> game.tournamentResult.descr,
      "year" -> game.start.getYear,
      "month" -> game.start.getMonth.getValue,
      "day" -> game.start.getDayOfMonth,
      "content" -> xml)
    games.insertOne(doc).results()
    log(s"Game ${game.id} saved to database")
  }

  private def xstream = {
    val xstream = new XStream
    val conv = new LocalDateTimeXMLConverter
    xstream.registerConverter(conv)
    xstream
  }

  private lazy val mongoClient: MongoClient = MongoClient()
  
  def games = {
    val database: MongoDatabase = mongoClient.getDatabase("test")
    database.getCollection("games")
  }

  def invalidGames = {
    val database: MongoDatabase = mongoClient.getDatabase("test")
    database.getCollection("invalid")
  }

  def load(id: Long): Either[Option[Game], String] = {
    val invalid = isInvalid(id)
    invalid match {
      case true => Right("")
      case false => {
        val doc = games.find(equal("gameId", id)).first().results().toList
        doc match {
          case Nil =>
            log(s"Game $id not found in database"); Left(None)
          case _ => {
            val xml = doc(0).get("content").get.asString.getValue
            log(s"Game $id found. No need to fetch")
            Left(Some(xstream.fromXML(xml).asInstanceOf[Game]))
          }
        }
      }
    }

  }

  def cleanup(name: String) = {
    log(s"delete all games from tournament $name from database")
    games.deleteMany(equal("tournament", name)).results
  }

  def cleanupAll = {
    log(s"delete all games from database")
    games.deleteMany(notEqual("gameId", "1")).results
  }

  def findAllTournaments = {
    val tournaments = games.find(notEqual("name", "None")).projection(include("tournament")).results().map(_.get("tournament")).distinct
    tournaments.map(_.get.asString.getValue)
  }

  def loadAllGames = {
    games.find(notEqual("gameId", "1")).results().map(game => {
      val xml = game.get("content").get.asString.getValue
      xstream.fromXML(xml).asInstanceOf[Game]
    }).toList
  }
  
  def loadAllInvalidGames = {
    invalidGames.find(notEqual("gameId", "1")).results().map(game => {
      val xml = game.get("gameId").get.asInt64.getValue
      val reason = game.get("reason").get.asString.getValue
      (xml, reason)
    }).toList
  }
}