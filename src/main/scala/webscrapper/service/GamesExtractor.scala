package webscrapper.service

import org.jsoup.nodes.Document
import webscrapper.GameSource
import scala.collection.JavaConversions.iterableAsScalaIterable
import webscrapper.InvalidGame

object GamesExtractor extends GameService {
  
  val location = "ul"
  
  def extractGames(year:Int, month:Int, day:Int) = {
    log(s"Extracting games from $year\\$month\\$day")
    val document: Document = GameSource.fromUrl(s"https://mafiaonline.ru/games/end_game.php?this_year=$year&this_month=$month&this_day=$day&ul=$location").get
    val table = document.select("table table").get(2)
    val rows = table.select("tr")
    val filtered = for {
          row <- rows
          g = row.select("td").get(2).text()
          if !g.contains("Сумеречный")
        } yield row
     val gameIds = filtered.map(_.select("td").get(1).select("a").attr("href").substring(5)).toList
     val games = gameIds.map(id => loadGame(id)).toList
     val notLoaded = gameIds diff games.filter(_.isDefined).map(_.get.id.toString)
     notLoaded
  }
  
  //def loadGames()
}