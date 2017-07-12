package webscrapper.database

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.MySQLDriver.api._
import webscrapper.util.Logger
import scala.concurrent.Await
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import webscrapper.Game
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.collection.mutable.ListBuffer
import slick.jdbc.ResultSetType
import slick.jdbc.ResultSetConcurrency
import slick.dbio.DBIOAction
import slick.driver.MySQLDriver
import webscrapper.Location
import java.time.LocalDateTime
import webscrapper.Role
import webscrapper.TournamentResult
import webscrapper.filesystem.FileSystem
import webscrapper.parser.LogParser
import webscrapper.GameSource
import webscrapper.filesystem.FileSystem
import scala.concurrent.Future

trait Repository extends FileSystem with Logger {
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

  private def enableStream(statement: java.sql.Statement): Unit = {
    statement match {
      case s: com.mysql.jdbc.StatementImpl => {
        s.enableStreamingResults()
      }
      case _ =>
    }
  }

  def addInvalid(id: Long, reason: String) : Unit = {
    val query = DBIO.seq((invalidGames += (0, id.toInt, reason)))
    await(database.run(query), 30)
    log(s"Game ${id} is invalid for $reason and saved to database")
  }

  def isInvalid(id: Long) : Boolean = {
    val query = invalidGames.filter(_.gameId === id.toInt).result
    val result = await(database.run(query), 30).size > 0
    if (result) log(s"Game ${id} is invalid ")
    result
  }

  def save(game: Game) : Unit = {
    val query = DBIO.seq((games += (0, game.id.toInt, game.countPlayers, game.players.map(p => p.name + "(" + p.role.role + ")").mkString(","), game.location.name, game.countRounds, game.result.descr, game.tournamentResult.descr, game.start.getYear, game.start.getMonth.getValue, game.start.getDayOfMonth)))
    await(database.run(query), 30)
    log(s"Game ${game.id} saved to database")
  }

  def load(id: Long): Either[String, Option[Int]] = {
    val invalid = isInvalid(id)
    invalid match {
      case true => Left("Game is invalid")
      case false => {
        val query = games.filter(_.gameId === id.toInt).map(_.gameId).result
        val result = database.run(query)
        val ids = await(result,30)
        ids match {
          case Nil => log(s"Game $id not found in database"); Right(None)
          case game => Right(ids.headOption)
        }
      }
    }
  }
  
  private def await[A](future:Future[A], seconds:Int) : A = Await.result(future, Duration(seconds, TimeUnit.SECONDS)).value

  def cleanupAll = {
    log(s"delete all games from database")
    val query = games.delete
    await(database.run(query), 30)
  }

  def searchGames(location: Location, results: List[TournamentResult], roles: List[Role], player: String, players: List[String], startPl: Int = 6, endPl: Int = 30, startR: Int = 1, endR: Int = 1000, sy: Int = 2012, ey: Int = 2017, sm: Int = 0, em: Int = 12, sd: Int = 0, ed: Int = 32) = {
    val filtered2 = if (location != Location.SUMRAK) games.filter(_.location === location.name) else games
    val filtered3 = if (startPl > 0) filtered2.filter(_.playersSize >= startPl) else filtered2
    val filtered4 = if (endPl > 0) filtered3.filter(_.playersSize <= endPl) else filtered3
    val filtered5 = if (startR > 0) filtered4.filter(_.rounds >= startR) else filtered4
    val filtered6 = if (endR > 0) filtered5.filter(_.rounds <= endR) else filtered5
    val filtered7 = if (results != Nil) {
      filtered6.filter(a => a.tournamentResult.inSet(results.map(r => r.descr)))
    } else filtered6
    val filtered8 = filtered7.filter(a => (a.year === sy && a.month >= sm && a.day >= sd) || (a.year === ey && a.month <= em && a.day <= ed) || (a.year > sy && a.year < ey))
    val filtered9 = if (player != null && !player.isEmpty) filtered8.filter(_.players like s"%$player%") else filtered8
    val filtered14 = filtered9.map(a => (a.gameId, a.location, a.tournamentResult, a.players, a.playersSize, a.rounds, a.year, a.month, a.day))
    stream(filtered14.result) { game =>
      {
        val pl = parsePlayersWithRoles(game._4)
        val found = if (player == null || player.isEmpty) true
        else {
          if (pl.filter(_._1 == player).size > 0) {
            val pla = pl.filter(_._1 == player)(0)
            if (roles.isEmpty || roles.contains(pla._2)) {
              if (!players.isEmpty) {
                val ingame = players.map(p => pl.filter(_._1 == p).size > 0).filter(_ != false)
                if (ingame.size < players.size) false else true
              } else true
            } else false
          } else false
        }
        val add = (game._1, game._2, game._3, parsePlayers(game._4).mkString(","), game._5, game._6, "" + game._7 + "-" + game._8 + "-" + game._9)
        if (found) add
      }
    }
  }

  private def parsePlayersWithRoles(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = even(arr)
    val od = odd(arr)
    val zipped = od.zip(ev).map(e => e._1 -> Role.getByRoleName(e._2))
    zipped
  }

  def loadPlayers(year: Int) = {
    val filtered = games.filter(_.year === year).sortBy(_.gameId.asc).map(g => g.players -> g.result)

    stream(filtered.result)(identity)
  }

  def loadforLocation(year: Int, location: Location, month: Int = 0) = {
    var filtered1 = games.filter(_.year === year).filter(_.location === location.name)
    if (month > 0) {
      filtered1 = filtered1.filter(_.month === month)
    }
    val filtered = filtered1.map(g => (g.playersSize, g.players, g.tournamentResult))
    stream(filtered.result)(identity)
  }

  def loadAllPlayers() = {
    val result = stream(games.map(_.players).result) {
      text => parsePlayers(text)
    }
    result.groupBy(identity)
      .filter(_._2.size > 50)
      .map(_._1)
      .toList
      .distinct
  }

  def loadGames(year: Int = 0, loc: Location = Location.SUMRAK, name: String = "", minCount: Int = 0) : List[Game] = {
    var condition = if (year > 0) games.filter(_.year === year) else games
    //var condition = if (month > 0) games.filter(_.month === month) else condition
    condition = if (loc != Location.SUMRAK) condition.filter(_.location === loc.name) else condition
    condition = if (minCount > 0) condition.filter(_.playersSize >= minCount) else condition
    condition = if (!name.isEmpty) condition.filter(_.players like s"%$name%") else condition
    val filtered = condition.map(_.gameId)
    val ids = stream(filtered.result)(identity)
    ids.map(id => LogParser.parse(readGame(id).get).get).map(_.toGame)
  }

  private def stream[A, B](action: MySQLDriver.StreamingDriverAction[Seq[A], A, Effect.Read])(mapping: A => B) = {
    val stream = database.stream(action.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize = Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[B]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream)
      .runForeach(text => {
        val mapped = mapping(text)
        mapped match {
          case list: List[B] => result ++= mapped.asInstanceOf[List[B]]
          case elem: B       => result += mapped
        }

      })
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result.toList
  }

  private def parsePlayers(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = odd(arr)
    val p = ev.toList.sortBy(identity)
    p
  }

  private def odd[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if (i % 2) == 0 => e }
  private def even[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if (i % 2) == 1 => e }
}

object DB {
  lazy val repo = new Repository{}
  def apply() = {
    repo
  }
}