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
import webscrapper.database.Filters._
import com.typesafe.config.ConfigFactory

trait Repository extends FileSystem with Logger {
  val ds = new ComboPooledDataSource
  val conf = ConfigFactory.load()
  ds.setDriverClass(conf.getString("db.driver"))
  ds.setJdbcUrl(conf.getString("db.url"))
  ds.setUser(conf.getString("db.login"))
  ds.setPassword(conf.getString("db.password"))
  ds.setMaxPoolSize(conf.getInt("db.poolSize"))

  val database = Database.forDataSource(ds)

  val games = TableQuery[Games]
  val invalidGames = TableQuery[InvalidGames]

  def addInvalid(id: Long, reason: String): Unit = {
    run(invalidGames += (0, id.toInt, reason))
    log(s"Game ${id} is invalid for $reason and saved to database")
  }

  def isInvalid(id: Long): Boolean = {
    val result = run(invalidGames.filter(_.gameId === id.toInt).result).size > 0
    if (result) log(s"Game ${id} is invalid ")
    result
  }

  def save(game: Game): Unit = {
    run(games += (makeGame(game)))
    log(s"Game ${game.id} saved to database")
  }

  def exist(id: Int): Either[String, Option[Boolean]] = {
    isInvalid(id) match {
      case true => Left("Game is invalid")
      case false => {
        val ids = run(games.filter(_.gameId === id).map(_.gameId).result)
        ids match {
          case Nil => log(s"Game $id not found in database"); Right(None)
          case game => Right(Option(true))
        }
      }
    }
  }

  def cleanupAll = {
    log(s"delete all games from database")
    run(games.delete)
  }

  def searchGames(location: Location = Location.SUMRAK, results: List[TournamentResult] = Nil, roles: List[Role] = Nil, player: Option[String] = None,
                  players: List[String] = Nil, startPl: Int = 6, endPl: Int = 30, startR: Int = 1, endR: Int = 1000,
                  sy: Int = 2012, ey: Int = 2017, sm: Int = 0, em: Int = 12, sd: Int = 0, ed: Int = 32) = {

    val query = applyFilter( 
      games => {
        List(
          locationFilter(location)(games),
          plSizeStartFilter(startPl)(games),
          plSizeEndFilter(endPl)(games),
          roundSizeStartFilter(startR)(games),
          roundSizeEndFilter(endR)(games),
          resultsFilter(results)(games),
          dateFilter(sy, ey, sm, em, sd, ed)(games),
          playerFilter(player)(games)
        )
      }
    ).map(a => (a.gameId, a.location, a.tournamentResult, a.players, a.playersSize, a.rounds, a.year, a.month, a.day)).result
    query.statements.foreach(println)
    stream(query){ game =>
      {
        val parsed = parsePlayers(game._4)
        val needInclude = for {
          p <- player
          found <- parsed.find(_._1 == p)
          if (rolesOk(roles, found))
          if (playersOk(players,parsed))
        } yield true
        if (!player.isDefined || needInclude.isDefined) Some(game._1, game._2, game._3, parsePlayers(game._4).map(_._1).mkString(","),
                 game._5, game._6, "" + game._7 + "-" + game._8 + "-" + game._9) else None 
      }
    }.filter(_.isDefined).map(_.get)
  }  
  
  def loadPlayers(year: Int) = {
    val query = games.filter(_.year === year)
      .sortBy(_.gameId.asc)
      .map(g => g.players -> g.result).result
    stream(query)(identity)
  }

  def loadforLocation(year: Int, location: Location, month: Int = 0) = {
    val query = applyFilter(games => {
      implicit val g = games
      List(
        filter(locationCondition(location)),
        filter(yearCondition(year)),
        monthFilter(month)
      )
    }).map(g => (g.playersSize, g.players, g.tournamentResult)).result
    stream(query)(identity)
  }

  def loadAllPlayers() = {
    val result = stream(games.map(_.players).result)(identity).flatMap(parsePlayers).map(_._1)
    result.groupBy(identity)
      .filter(_._2.size > 50)
      .map(_._1)
      .toList
      .distinct
  }

  def loadGames(year: Int = 0, loc: Location = Location.SUMRAK, name: String = "", minCount: Int = 0): List[Game] = {
    val query = applyFilter( 
      games => {
        List(
          locationFilter(loc)(games),
          yearFilter(year)(games),
          plSizeStartFilter(minCount)(games),
          playerFilter(Some(name))(games)
        )
      }
    ).map(_.gameId).result
    val ids = stream(query)(identity)
    for {
      id <- ids
      game <- readGame(id)
      parsed <- LogParser.parse(game)
      replayed <- parsed.run
    } yield replayed
  }

  private def stream[A, C <: Effect, B](action: DBIOAction[Seq[A], Streaming[A], C])(mapping: A => B) = {
    val stream = database.stream(action.withStatementParameters(rsType = ResultSetType.ForwardOnly, rsConcurrency = ResultSetConcurrency.ReadOnly, fetchSize = Integer.MIN_VALUE, statementInit = enableStream))
    implicit val system = ActorSystem("Sys")

    implicit val materializer = ActorMaterializer()
    val result = new ListBuffer[B]()
    val future = akka.stream.scaladsl.Source.fromPublisher(stream).runForeach(result += mapping(_))
    future.onComplete(_ => system.shutdown())(system.dispatcher)
    Await.result(future, Duration.Inf)
    result.toList
  }

  private def enableStream(statement: java.sql.Statement): Unit = {
    statement match {
      case s: com.mysql.jdbc.StatementImpl => {
        s.enableStreamingResults()
      }
      case _ =>
    }
  }

  private def applyFilter(op : Games => List[Option[Rep[Boolean]]]) = {
    games.filter(g => {
      op(g).collect { case Some(criteria) => criteria }.reduceLeft(_ && _)
    })
  }
  
  private def run[A, B <: NoStream, C <: Effect](action: DBIOAction[A, B, C]) = {
    val query = action
    await(database.run[A](query), 30)
  }

  private def await[A](future: Future[A], seconds: Int): A = Await.result(future, Duration(seconds, TimeUnit.SECONDS)).value

  private def rolesOk(roles:List[Role], found:(String, Role)) = roles == Nil || roles.isEmpty || roles.contains(found._2)

  private def playersOk(players: List[String], parsed: List[(String, Role)]) = players == Nil || players.isEmpty || allPlayersPresent(players, parsed)
  
  private def allPlayersPresent(players: List[String], parsed: List[(String, Role)]) = {
    val ingame = players.map(p => parsed.filter(_._1 == p).size > 0).filter(_ != false)
    ingame.size >= players.size
  }
  
  private[database] def parsePlayers(players: String) = {
    val regex = "(.+?)\\((.+?)\\)".r
    val gamers = players.replaceAll("Любит на 99,9", "Любит на 99.9").split(",")
    gamers.map { 
      case regex(name, role) => (name.replaceAll("Любит на 99.9", "Любит на 99,9"), Role.get(role)) 
      case x => println(x); ("", Role.BOSS)
    }.toList
  }
  
  private def makeGame(game:Game) = {
    ( 0, game.id.toInt, game.countPlayers, game.players.map(p => p.name + "(" + p.role.role + ")").mkString(","),
    game.location.name, game.countRounds, game.result.descr, game.tournamentResult.descr, game.start.getYear,
    game.start.getMonth.getValue, game.start.getDayOfMonth)
  }
}

object Filters {
  private def conditionalFilter( op: => Boolean)( cond: => Rep[Boolean]) = if(op) Some(cond) else None
  
  private[database] def filter( cond: => Rep[Boolean]) = conditionalFilter(true)(cond)
  
  private[database] def locationCondition(location:Location)(implicit g:Games) = g.location === location.name
  
  private[database] def yearCondition(year:Int)(implicit g:Games) = g.year === year
  
  private[database] def locationFilter(location:Location)(implicit g:Games) = conditionalFilter(location != Location.SUMRAK)(locationCondition(location))
  
  private[database] def plSizeStartFilter(startPl:Int)(implicit g:Games) = conditionalFilter(startPl > 0)(g.playersSize >= startPl)
  
  private[database] def plSizeEndFilter(endPl:Int)(implicit g:Games) = conditionalFilter(endPl > 0)(g.playersSize <= endPl)
  
  private[database] def roundSizeStartFilter(startR:Int)(implicit g:Games) = conditionalFilter(startR > 0)(g.rounds >= startR)
  
  private[database] def roundSizeEndFilter(endR:Int)(implicit g:Games) = conditionalFilter(endR > 0)(g.rounds <= endR)
  
  private[database] def resultsFilter(results:List[TournamentResult])(implicit g:Games) = conditionalFilter(!results.isEmpty)(g.tournamentResult.inSet(results.map(r => r.descr)))
  
  private[database] def dateFilter(sy:Int, ey:Int, sm:Int, em:Int, sd:Int, ed:Int)(implicit g:Games) = filter(yearsFilter(g, sy, ey, sm, em, sd, ed))
  
  private[database] def playerFilter(player:Option[String])(implicit g:Games) = conditionalFilter(player.isDefined && !player.get.isEmpty)(g.players like s"%${player.get}%")
  
  private[database] def monthFilter(month:Int)(implicit g:Games) = conditionalFilter(month > 0)(g.month === month)
  
  private[database] def yearFilter(year:Int)(implicit g:Games) = conditionalFilter(year > 0)(yearCondition(year))
  
  private def yearsFilter(g: Games, sy: Int, ey: Int, sm: Int, em: Int, sd: Int, ed: Int) = {
    val gtThenStartDate = g.year === sy && g.month >= sm && g.day >= sd
    val ltThenEndDate = g.year === ey && g.month <= em && g.day <= ed
    val inBetweenStartAndEnd = g.year > sy && g.year < ey
    if(sy == ey) (gtThenStartDate && ltThenEndDate) || inBetweenStartAndEnd
    else (gtThenStartDate || ltThenEndDate) || inBetweenStartAndEnd
  }
}

object DB {
  lazy val repo = new Repository {}
  def apply() = {
    repo
  }
}