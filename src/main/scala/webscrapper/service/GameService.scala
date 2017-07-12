package webscrapper.service

import org.jsoup.nodes.Document
import webscrapper.GameSource
import webscrapper.parser.LogParser
import webscrapper.Properties
import java.util.concurrent.Executors
import webscrapper.Part
import webscrapper.Game
import java.util.concurrent.ExecutorService
import scala.collection.JavaConversions._
import java.util.concurrent.FutureTask
import java.util.concurrent.Callable
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.Future
import webscrapper.Analyser
import webscrapper._
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import webscrapper.filesystem.FileSystem
import webscrapper.util.Logger
import webscrapper.database.Repository

class GameService extends Repository with Logger with FileSystem {

  val mafiaLogPath = "http://mafiaonline.ru/log"

  // val loadedTournaments = new MutableMap[String, Tournament]

  def loadGame(url: String, useCache: Boolean = true): Option[Game] = {
    require(!url.isEmpty && url.length >= 7)
    val id = url.substring(url.length - 7).toLong
    log(s"Fetching game $id")
    val game = if (useCache) load(id) else Left(None)
    game match {
      case Left(_) => Some(InvalidGame)
      case Right(option) => option match {
        case None => {
          val fetched = Try(fetchGameRemotely(id, useCache))
          fetched match {
            case Success(loaded) =>
              log(s"Game $id is fetched"); loaded
            case Failure(f) => {
              f.printStackTrace
              log(s"Error encountered $f")
              f match {
                case e: StoppedException => addInvalid(id, e.getMessage)
                case e: Exception        => e.printStackTrace
              }
              None
            }
          }
        }
        case Some(x) => Some(LogParser.parse(readGame(x).get, false).get.run.get)
      }
    }
  }

  /*  def createTournament(name: String) = loadedTournaments.getOrElseUpdate(name, buildTournament(name))

  def createAnalyzer = buildAnalyzer("Default")

  private def buildAnalyzer(name: String, file: String = "/games.txt") = {
    log(s"Building analyser $name")
    log(s"retrieve game numbers from $file ...")
    val root = Properties.path
    val parts = parseParts(s"$root$name$file")
    val ids = parts.flatMap(_.games)
    val games = retrieveGames(ids, name)
    log(s"all games loaded")
    new Analyser(games, name)
  }

  private def buildTournament(name: String) = {
    log(s"Building tournament $name")
    val rootPath = Properties.path
    val teams = parseTeams(rootPath + name + "/teams.txt")
    log(s"teams are successfully loaded $teams")
    val parts = parseParts(rootPath + name + "/games.txt")
    log(s"tournament parts are successfully loaded $parts")
    val penalties = parsePenalties(rootPath + name + "/penalties.txt", teams)
    log(s"penalties are successfully loaded $penalties")
    val games = retrieveGames(parts.flatMap(_.games), name)
    val partsWithGames = parts.map(p => new Part(p.name, games.filter(g => p.games.contains(g.id)), name))
    log(s"all games loaded")
    new Tournament(name, teams, partsWithGames, penalties)
  }

  def retrieveGames(ids: List[Long], tournamentName: String = "Default") = {
    log("Fetching games:" + ids.mkString(","))
    val start = System.nanoTime
    val games = ids.par.map { id => loadGame(id.toString) }.flatten.filter(_ != InvalidGame).seq.toList
    val loadedIds = games.map(_.id)
    val end = System.nanoTime
    log("Execution time + " + (end - start).toDouble / 1000000000 + " сек")
    log("Games that successfully loaded:" + loadedIds.mkString(","))
    log("Games that not loaded:" + (ids diff loadedIds).mkString(","))
    games
  }

  def cleanupAll = DAO.cleanupAll*/

  private def fetchGameRemotely(id: Long, useCache: Boolean = true): Option[Game] = {
    val game = for {
      _ <- info(s"Loading $id from mafia site")
      doc <- GameSource.fromUrl(s"$mafiaLogPath/$id")
      _ <- info(s"Game $id is loaded")
      _ <- info("Parsing game" + id)
      _ <- saveGameSource(id)(doc).toOption
      parsed <- LogParser.parse(doc)
      g <- parsed.run
      _ <- info(s"Game $id parsed")

    } yield (g)
    if (useCache) save(game.get)
    game
  }
  
  def simulate(id: Long, order: Int = (-1)) = {
    for {
      _ <- info(s"Loading $id from mafia site")
      doc <- GameSource.fromUrl(s"$mafiaLogPath/$id")
      _ <- info(s"Game $id is loaded")
      _ <- info(s"Parsing game $id ...")
      simulated <- LogParser.simulate(doc, order)
    } yield(simulated)
  }
}
