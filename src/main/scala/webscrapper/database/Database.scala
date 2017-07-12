/*package webscrapper.database

import com.thoughtworks.xstream.XStream
import com.amarkhel.LocalDateTimeXMLConverter
import slick.driver.MySQLDriver.api._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import webscrapper.util.Logger._
import webscrapper.Game
import java.util.Base64
import java.nio.charset.StandardCharsets
import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.backend.DatabasePublisher
import java.util.stream.Sink
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import scala.collection.mutable.ListBuffer
import webscrapper.Role
import slick.jdbc.ResultSetType
import slick.jdbc.ResultSetConcurrency
import slick.jdbc.ResultSetHoldability
import webscrapper.Location
import webscrapper.TournamentResult
import java.time.LocalDateTime

object DAO {
  private def xstream = {
    val xstream = new XStream
    val conv = new LocalDateTimeXMLConverter
    xstream.registerConverter(conv)
    xstream
  }
  val ds = new ComboPooledDataSource
  ds.setDriverClass("com.mysql.jdbc.Driver")
  ds.setJdbcUrl("jdbc:mysql://localhost:3306/scala?dontTrackOpenResources=true&useUnicode=true&characterEncoding=UTF-8")
  ds.setUser("root")
  ds.setPassword("Cherry2015#")
  ds.setMaxPoolSize(100)

  val database = {
    Database.forDataSource(ds)
  }
  val games = TableQuery[Games]
  val invalidGames = TableQuery[InvalidGames]

  def enableStream(statement: java.sql.Statement): Unit = {
    statement match {
      case s: com.mysql.jdbc.StatementImpl => {
        s.enableStreamingResults()
      }
      case _ =>
    }
  }

  def gamesToFile = {
    val q = for (g <- games) yield g.id
    val a = q.result
    val p: DatabasePublisher[String] = database.stream(a)

// .foreach is a convenience method on DatabasePublisher.
// Use Akka Streams for more elaborate stream processing.
p.foreach { s => println(s"Element: $s") }

    val file = new File("games_from_db.txt")
    val bw = new BufferedWriter(new FileWriter(file))

    val query = for (g <- games) yield (g.gameId, g.playersSize, g.players, g.location, g.rounds, g.result, g.tournamentResult, g.year, g.month, g.day)
    val stream = database.stream(query.result.withStatementParameters(statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()

    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      println(text);
      bw.write(text.productIterator.mkString(";") + "\n")
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)

    Await.result(future, Duration.Inf)

    bw.close()
  }

  def addInvalid(id: Long, reason: String) = {
    val setup = DBIO.seq((invalidGames += (0, id.toInt, reason)))
    Await.result(database.run(setup), Duration(30, TimeUnit.SECONDS)).value
    //database.close
    log(s"Game ${id} is invalid for $reason and saved to database")
  }

  def isInvalid(id: Long) = {
    val query = invalidGames.filter(_.gameId inSet (Set(id.toInt)))
    val setup = query.result
    val result = Await.result(database.run(setup), Duration(30, TimeUnit.SECONDS)).value.size > 0

    if (result) log(s"Game ${id} is invalid ")
    //database.close
    result
  }

  def save(game: Game) = {
    val setup = DBIO.seq((games += (0, game.id.toInt, game.countPlayers, game.players.mkString(","), game.location.name, game.countRounds, game.result.descr, game.tournamentResult.descr, game.start.getYear, game.start.getMonth.getValue, game.start.getDayOfMonth)))
    Await.result(database.run(setup), Duration(30, TimeUnit.SECONDS)).value
    //database.close
    log(s"Game ${game.id} saved to database")
  }

  def load(id: Long): Either[Option[Game], String] = {
    val invalid = isInvalid(id)
    invalid match {
      case true => Right("")
      case false => {
        val c = games.filter(_.gameId === id.toInt).map(_.content)
        val res = database.run(c.result)
        val doc = Await.result(res, Duration(30, TimeUnit.SECONDS)).value
        //database.close
        doc match {
          case Nil =>
            log(s"Game $id not found in database"); Left(None)
          case seq => {
            val xml = decode(seq(0))
            log(s"Game $id found. No need to fetch")
            Left(Some(xstream.fromXML(xml).asInstanceOf[Game]))
          }
        }
      }
    }

  }

  def cleanupAll = {
    log(s"delete all games from database")
    val q = games.delete
    Await.result(database.run(q), Duration(30, TimeUnit.SECONDS)).value
    //database.close
  }

  def findAllTournaments = {
    val tournaments = games.find(notEqual("name", "None")).projection(include("tournament")).results().map(_.get("tournament")).distinct
    tournaments.map(_.get.asString.getValue)
  }

  def loadAllGames = {
    val query = games.map(_.content)
    val res = database.run(query.result)
    val c = Await.result(res, Duration(30, TimeUnit.SECONDS)).value
    //database.close
    c.map(g => xstream.fromXML(decode(g)).asInstanceOf[Game]).toList
  }

  def loadGames(year: Int, month: Int) = {
    val query = games.filter(_.year === year).filter(_.month === month).map(_.content)
    val res = database.run(query.result)
    val c = Await.result(res, Duration(300, TimeUnit.SECONDS)).value
    //database.close
    c.map(g => xstream.fromXML(decode(g)).asInstanceOf[Game]).toList
  }

  def loadGames2(year: Int, month: Int) = {
    val filtered = games.filter(_.year === year).filter(_.month === month)
    val query = for (g <- filtered) yield (g.content)
    val stream = database.stream(query.result.withStatementParameters(statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[Game]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      result += xstream.fromXML(decode(text)).asInstanceOf[Game]
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result
  }

  def loadGames2(year: Int) = {
    val filtered = games.filter(_.year === year).filter(_.month < 9).map(_.content)

    val stream = database.stream(filtered.result.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize=Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[(Role, String)]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      val game = xstream.fromXML(decode(text)).asInstanceOf[Game]
      val messages = game.statistics.messageStats.messages.flatMap(_.transform)
      result ++= messages
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result
  }
  
  def loadGamesFor(name:String) = {
    val filtered = games.filter(g=> g.year === 2016 || g.year===2015).filter(_.players like s"%$name%").map(_.content)

    val stream = database.stream(filtered.result.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize=Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[(Role, String)]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      val game = xstream.fromXML(decode(text)).asInstanceOf[Game]
      val messages = game.statistics.messageStats.messages.flatMap(_.transform)
      result ++= messages
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result
  }
  
  def loadPlayers(year:Int) = {
    val filtered = games.filter(_.year === year).sortBy(_.gameId.asc).map(g => g.players -> g.result)

    val stream = database.stream(filtered.result.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize=Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[(String, String)]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      result += text
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result
  }
  
  def loadforLocation(year:Int, location:Location, month:Int =0) = {
    var filtered1 = games.filter(_.year === year).filter(_.location === location.name)
    if (month > 0) {
      filtered1 = filtered1.filter(_.month === month)
    }
    val filtered = filtered1.map(g => (g.playersSize, g.players, g.tournamentResult))

    val stream = database.stream(filtered.result.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize=Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[(Int, String, String)]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      result += text
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result
  }
  
  def loadGames(year:Int=0, loc:Location=Location.SUMRAK, name:String="", minCount:Int = 0) = {
    val filtered = if(year > 0) games.filter(_.year === year) else games
    val filtered2 = if(loc != Location.SUMRAK) filtered.filter(_.location === loc.name) else filtered
    val filtered3 = if(minCount > 0) filtered2.filter(_.playersSize >= minCount) else filtered2
    val filtered4 = if(!name.isEmpty) filtered3.filter(_.players like s"%$name%") else filtered3
    val filtered5 = filtered4.map(_.content)
    val stream = database.stream(filtered5.result.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize=Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[Game]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      val game = xstream.fromXML(decode(text)).asInstanceOf[Game]
      result += game
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result.toList
  }

  def decode(txt: String) = {
    new String(Base64.getDecoder.decode(txt.getBytes(StandardCharsets.UTF_8)))
  }

  def encode(txt: String) = {
    Base64.getEncoder.encodeToString(txt.getBytes(StandardCharsets.UTF_8))
  }
  
  def loadAllPlayers() = {
    val filtered = games.map(_.players)
    val stream = database.stream(filtered.result.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize=Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[String]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(text => {
      val game = parsePlayers(text)
      
      result ++= game
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result.groupBy(identity).filter(_._2.size > 50).map(_._1).toList.distinct
  }
  
  private def parsePlayers(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = odd(arr)
    val p =ev.toList.sortBy(identity)
    p
  }
  
  private def parsePlayersWithRoles(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = even(arr)
    val od = odd(arr)
    val zipped = od.zip(ev).map(e => e._1 -> Role.getByRoleName(e._2))
    zipped
  }
  
  private def odd[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if (i % 2) == 0 => e }
  private def even[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if (i % 2) == 1 => e }

  def searchGames(location: Location, results: List[TournamentResult], roles: List[Role], player:String, players: List[String], startPl: Int = 6, endPl: Int = 30, startR: Int = 1, endR: Int = 1000, sy:Int=2012, ey:Int=2017, sm:Int = 0, em:Int = 12, sd:Int = 0, ed:Int = 32) = {
    //val filtered = if(year > 0) games.filter(_.year === year) else games
    val filtered2 = if(location != Location.SUMRAK) games.filter(_.location === location.name) else games
    val filtered3 = if(startPl > 0) filtered2.filter(_.playersSize >= startPl) else filtered2
    val filtered4 = if(endPl > 0) filtered3.filter(_.playersSize <= endPl) else filtered3
    val filtered5 = if(startR > 0) filtered4.filter(_.rounds >= startR) else filtered4
    val filtered6 = if(endR > 0) filtered5.filter(_.rounds <= endR) else filtered5
    val filtered7 = if(results != Nil) {
      filtered6.filter(a => a.tournamentResult.inSet(results.map(r => r.descr))) 
    } else filtered6
    //val (startYear, startMonth, startDay) = if (start != null) (start.getYear, start.getMonthValue, start.getDayOfMonth) else (-1, -1,-1)
    //val (endYear, endMonth, endDay) = if (end != null) (end.getYear, end.getMonthValue, end.getDayOfMonth) else (-1, -1,-1)
    val filtered8 = filtered7.filter(a => (a.year === sy && a.month >= sm && a.day >= sd) || (a.year===ey && a.month <= em && a.day <= ed) || (a.year > sy && a.year<ey))
    val filtered9 = if(player != null && !player.isEmpty) filtered8.filter(_.players like s"%$player%") else filtered8
    val filtered14 = filtered9.map(a => (a.gameId, a.location, a.tournamentResult, a.players, a.playersSize, a.rounds, a.year ,a.month ,a.day))
    //val filtered4 = if(!name.isEmpty) filtered3.filter(_.players like s"%$name%") else filtered3
    //val filtered5 = filtered3
    val stream = database.stream(filtered14.result.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize=Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[(Int, String, String, String, Int, Int, String)]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(game => {
      val pl = parsePlayersWithRoles(game._4)
      val found = if(player==null || player.isEmpty) true 
      else {
        if (pl.filter(_._1 == player).size > 0) {
          val pla = pl.filter(_._1 == player)(0)
          if(roles.isEmpty || roles.contains(pla._2)) {
            if (!players.isEmpty) {
              val ingame = players.map(p => pl.filter(_._1 == p).size > 0).filter(_ != false)
              if(ingame.size < players.size) false else true
            } else true
          } else false
        } else false
      }
      val add = (game._1, game._2, game._3, parsePlayers(game._4).mkString(","), game._5, game._6, "" + game._7 + "-" +game._8 + "-" + game._9)
      if(found) result += add
    })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result
  }
}*/