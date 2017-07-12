/*package webscrapper.service

import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import webscrapper.TestUtils._
import org.jsoup.nodes.Document
import webscrapper.GameSource
import scala.collection.JavaConversions.iterableAsScalaIterable
import webscrapper.util.StatUtils
import scala.collection.mutable.ListBuffer
import webscrapper.InvalidGame
import scala.util.matching.Regex
import samples.DBTest
import webscrapper.database.DAO
import webscrapper.database.DAO


@RunWith(classOf[JUnitRunner])
class Test extends FunSuite with Matchers {

  test(""){
    println(MongoService.delete)
    println(MongoService.loadAllInvalid)
    val list ="2903536,2919936,2919946,2919951,2933173,2953158,2976615,2976624,3139390,3139390,3139390,3139390,3139390,3139390,3139390,3531736,3531738,3535379,3575315,3596566,3640986,3678092,3682518,3707309,3707310"
    val s = new GameService
    val list2 = list.split(",").filter(!s.loadGame(_).isDefined)
    val list3 = list.split(",")
    println(list3 diff list2 mkString(","))
    MongoService.addInvalid(2279794, "Все вышли по тайму")
    MongoService.addInvalid(2279790, "Все вышли по тайму")
    MongoService.addInvalid(2279784, "Все вышли по тайму")
    MongoService.addInvalid(2279782, "Все вышли по тайму")
    MongoService.addInvalid(2279781, "Все вышли по тайму")
    MongoService.addInvalid(2336082, "Все вышли по тайму")
    MongoService.addInvalid(2336075, "Все вышли по тайму")
    MongoService.addInvalid(2336072, "Все вышли по тайму")
    MongoService.addInvalid(2336071, "Все вышли по тайму")
    MongoService.addInvalid(2336070, "Все вышли по тайму")
    MongoService.addInvalid(2336062, "Все вышли по тайму")
    MongoService.addInvalid(2336044, "Все вышли по тайму")
    MongoService.addInvalid(2412814, "Все вышли по тайму")
    MongoService.addInvalid(2412809, "Все вышли по тайму")
    MongoService.addInvalid(2412806, "Все вышли по тайму")
    MongoService.addInvalid(2412788, "Все вышли по тайму")
    MongoService.addInvalid(2412781, "Все вышли по тайму")
    MongoService.addInvalid(2417087, "Все вышли по тайму")
    MongoService.addInvalid(2632189, "Все вышли по тайму")
    MongoService.addInvalid(2632186, "Все вышли по тайму")
    MongoService.addInvalid(2632184, "Все вышли по тайму")
    MongoService.addInvalid(2632174, "Все вышли по тайму")
    MongoService.addInvalid(2692771, "Все вышли по тайму")
    MongoService.addInvalid(2754992, "Все вышли по тайму")
    MongoService.addInvalid(2754986, "Все вышли по тайму")
    MongoService.addInvalid(2754976, "Все вышли по тайму")
    MongoService.addInvalid(2754973, "Все вышли по тайму")
    MongoService.addInvalid(2754945, "Все вышли по тайму")
    MongoService.addInvalid(2755607, "Все вышли по тайму")
    MongoService.addInvalid(2755599, "Все вышли по тайму")
    MongoService.addInvalid(2774367, "Все вышли по тайму")
    MongoService.addInvalid(2774365, "Все вышли по тайму")
    MongoService.addInvalid(2774356, "Все вышли по тайму")
    MongoService.addInvalid(2774344, "Все вышли по тайму")
    MongoService.addInvalid(2976655, "Все вышли по тайму")
    MongoService.addInvalid(2444696, "Все вышли по тайму")
    MongoService.addInvalid(2447227, "Все вышли по тайму")
    MongoService.addInvalid(2451965, "Все вышли по тайму")
    MongoService.addInvalid(2451958, "Все вышли по тайму")
        MongoService.addInvalid(2451953, "Все вышли по тайму")
        MongoService.addInvalid(2465672, "Все вышли по тайму")
        MongoService.addInvalid(2465663, "Все вышли по тайму")
        MongoService.addInvalid(2465654, "Все вышли по тайму")
        MongoService.addInvalid(2465646, "Все вышли по тайму")
        MongoService.addInvalid(2485589, "Все вышли по тайму")
        MongoService.addInvalid(2485583, "Все вышли по тайму")
        MongoService.addInvalid(2485580, "Все вышли по тайму")
        MongoService.addInvalid(2862550, "Все вышли по тайму")
        MongoService.addInvalid(2862547, "Все вышли по тайму")
        MongoService.addInvalid(2919974, "Все вышли по тайму")
        MongoService.addInvalid(2919967, "Все вышли по тайму")
        MongoService.addInvalid(2976654, "Все вышли по тайму")
        MongoService.addInvalid(2976653, "Все вышли по тайму")
        MongoService.addInvalid(2988137, "Все вышли по тайму")
    
    MongoService.addInvalid(2284461, "Ход мафии после окончания хода")
    MongoService.addInvalid(2285517, "Ход мафии после окончания хода")
    MongoService.addInvalid(2903536, "Глюк мафии")
    MongoService.addInvalid(2919951, "Глюк мафии")
    MongoService.addInvalid(2919946, "Глюк мафии")
    MongoService.addInvalid(2919936, "Глюк мафии")
    MongoService.addInvalid(2933173, "Глюк мафии")
    MongoService.addInvalid(2953158, "Глюк мафии")
    MongoService.addInvalid(2976624, "Глюк мафии")
    MongoService.addInvalid(2976615, "Глюк мафии")
    MongoService.addInvalid(2372744, "Ход мафа не отражен в логе")
    MongoService.addInvalid(2396384, "Ход мафа не отражен в логе")
    MongoService.addInvalid(2401896, "Ход мафа не отражен в логе")
    MongoService.addInvalid(2408637, "Ход мафа не отражен в логе")
    MongoService.addInvalid(2419330, "Ход мафа не отражен в логе")
    MongoService.addInvalid(2448595, "Ход мафа не отражен в логе")
    MongoService.addInvalid(2466947, "Ход мафа не отражен в логе")
    MongoService.addInvalid(2483614, "Ход мафа не отражен в логе")
    //MongoService.cleanupAll
    //MongoService.showIndexes
    //MongoService.cleanupAll
    //val year = 2012
    val missed = for (
        year <- 2012 to 2017;
        month <- 1 to 12;
        day <- 1 to 31
    ) yield GamesExtractor.extractGames(year, month, day).toList
    val m = missed.flatten.toList.filter(id => !DAO.isInvalid(id.toLong))
    println(m.mkString(","))
    //DAO.addInvalid(3373289, "Все вышли по тайму")
    //MongoService.groupResults
    //val a = new DBTest
    val x = List(1,2,3,4,5)
    val sum = List.foldLeft(x, 0)(_ + _)
    println(x)
    println(sum)
    println(List.add_1(x))
    println(List.add(x, 5))
    println(List.intToStr(x))
    println(List.map[Int, Double](x, y => y.toDouble + 0.12))
    println(List.filter[Int](x, a => a > 3))
    println(List.filterLeft[Int](x, a => a > 3))
    println(List.flatMap[Int, Double](x, a => List(a.toDouble + 0.1, a.toDouble + 0.2)))
    println(List.concat(List(1,2,3), List(4,5,6,7)))
    println(List.zipWith(List(1,2,3), List(4,5,6,7))(_ * _))
    val tree = new Branch(new Branch(new Branch(new Leaf(1), new Leaf(10)), new Leaf(2)), new Branch(new Leaf(3), new Leaf(4)))
    println(Tree.size(tree))
    println(Tree.maximum(tree))
    println(Tree.depth(tree))
    println(Tree.map[Int, Double](tree)(i => i.toDouble + 0.5))
    println(Tree.flatMap(tree)(i => new Branch(new Leaf(i), new Leaf(i))))
    def find(r:Regex, s:String) = {
    s match {
  case r(player1, player2) => List(player1.trim, player2.trim)
  case r(player1) => List(player1.trim)
  case _ => List()
  }
    }
    
  //val s = new GameService
  //println("2574936,2574934,2574927,2574923,2574921,2580381,2727416".split(",").filter(!s.loadGame(_).isDefined).mkString(","))
  //s.loadGame("2574927")
    val invalid = MongoService.loadAllInvalidGames
    println(invalid.size)
    for (i <- invalid if i._1 > 3535379) yield DAO.addInvalid(i._1, i._2)
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