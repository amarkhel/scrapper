/*package webscrapper.service

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.jsoup.nodes.Document
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import samples.DBTest
import webscrapper.database.DAO



@RunWith(classOf[JUnitRunner])
class Test extends FunSuite with Matchers {

  test(""){
    val s = new GameService
   
    val missed = for (
        month <- 1 to 5;
        day <- 1 to 31
    ) yield GamesExtractor.extractGames(2017, month, day).toList
    val m = missed.flatten.toList.filter(id => !DAO.isInvalid(id.toLong))
    println(m.mkString(","))
    
}
    
  //}
  test("2") {
    //val year = 2015
    //val month = 1
    val location = "ul1"
    val startDay = 1
    val endDay = 31
    //val count = 9
    val games = new ListBuffer[String]()
    for(year <- 2013 to 2017) {
    for (m <- 1 to 12) {
      val gameIds = for {
        i <- startDay to endDay
        document: Document = GameSource.fromUrl(s"http://mafiaonline.ru/games/end_game.php?this_year=$year&this_month=$m&this_day=$i&ul=$location")
        table = document.select("table table").get(2)
        rows = table.select("tr")
        filtered = for {
          row <- rows
          c = row.select("td").get(4)
          d = c.select("font")
          e = d.get(0)
          f = e.text.toInt
          //if (f == count)
        } yield row
        mapped = filtered.map(_.select("td").get(1).select("a").attr("href").substring(5))
      } yield mapped
      games ++= gameIds.flatten.toList
    }
    }
    println(games.mkString(","))
  }
  test("3"){
    val r = scala.util.Random
    val array = Array.range(1, 10)
    val digits = for (
        i <- 1 to 200000;
        first = r.nextInt(9);
        firstVal = array.apply(first);
        array2 = array.filter(_ != first);
        second = r.nextInt(8);
        secondVal = array2.apply(second)
        )
      yield (firstVal, secondVal)
    val a = digits.toList
    val list = a.flatMap(t => List(t._1, t._2))
    val map = list.groupBy(identity).map{case (k,v) => (k, v.size)}
    println(map)
    println("Распределение стандартное отклонение" + StatUtils.deviation3(map))
    
    val digits2 = for (
        i <- 1 to 400000;
        first = r.nextInt(9)
        )
      yield first
    val map2 = digits2.groupBy(identity).map{case (k,v) => (k, v.size)}
    println(map2)
    println("Распределение стандартное отклонение" + StatUtils.deviation3(map2))
  }
}*/