package samples
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import java.sql.Blob
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import webscrapper.service.MongoService
import com.thoughtworks.xstream.XStream
import com.amarkhel.LocalDateTimeXMLConverter
import webscrapper.database.Games
import webscrapper.util.Logger

class DBTest extends Logger {

  private def xstream = {
    val xstream = new XStream
    val conv = new LocalDateTimeXMLConverter
    xstream.registerConverter(conv)
    xstream
  }
  val database = Database.forURL("jdbc:mysql://localhost:3306/scala", "root", "Cherry2015", driver = "com.mysql.jdbc.Driver")
  val games = TableQuery[Games]
  try {
    val game = MongoService.load(3673446).left.get.get
    val xml = xstream.toXML(game)
    log(s"Game ${game.id} saved to database")
    val setup = DBIO.seq(
      (
        games += (0, game.id.toInt, game.countPlayers, game.players.mkString(","), game.location.name, game.countRounds, game.result.descr, game.tournamentResult.descr, game.start.getYear, game.start.getMonth.getValue, game.start.getDayOfMonth)))

    val setupFuture = database.run(setup)
    val a = Await.result(setupFuture, Duration(30, TimeUnit.SECONDS)).value
    // Read all coffees and print them to the console
    println("Games:")
    database.run(games.result).map(_.foreach {
      case (id, gameId, playersSize, players, location, rounds, result, tournamentResult, year, month, day) =>
        println("  " + id + "\t" + gameId + "\t" + players + "\t" + location + "\t")
    })
    database.run(games.delete)

  } finally database.close

  

}