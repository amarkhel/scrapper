package com.supergloo

import webscrapper.database.DB
import webscrapper.Location
import webscrapper.util.StatUtils
import webscrapper.TournamentResult
import scala.collection.mutable.HashMap
import webscrapper.Role
import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer

object PercentWinApp {

  def even[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if ((i + 1) % 2) == 0 => e }
  def odd[A](l: Array[A]) = l.zipWithIndex.collect { case (e, i) if ((i + 1) % 2) == 1 => e }

  def getPossiblePoints(isMaf: Boolean, map: Map[String, Double]) = {
    if (isMaf) map.get(TournamentResult.MAFIA_WIN.descr).get else map.get(TournamentResult.GOROD_WIN.descr).get
  }

  def printRating(location: Location, it: (String, Seq[(Int, String, Int, Double)])) = {
    println(s"[spoiler=Статистика ${location.name} - ${it._1}]")
    val top = it._2
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Место[/th][th]Ник[/th][th]Число игр[/th][th]КПД[/th][/tr]")
    top.foreach(res => {
      println(s"[tr][td]${res._1 + 1}[/td][td][nick]${res._2}[/nick][/td][td]${res._3}[/td][td]${res._4}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }

  def groupKrest(game: (Int, String, String)) = {
    val count = game._1
    groupSize(count)
  }

  def parsePlayers(players: String) = {
    val arr = players.split(",").map(_.replaceAll("name =", "")).map(_.replaceAll("role =", "")).map(_.trim).map(_.replaceAll("Любит на 99,9", "Любит на 99"))
    val ev = even(arr)
    val od = odd(arr)
    val zipped = od.zip(ev).map(e => e._1 -> Role.getByRoleName(e._2)).filter(_._2 != Role.MANIAC)
    zipped
  }
  
  def groupSize(count: Int) = {
    count match {
      case 6                            => "7"
      case 7                            => "7"
      case 8                            => "8"
      case 9                            => "9"
      case 12                           => "12"
      case 13                           => "13"
      case 21                           => "21"
      case 22                           => "21"
      case 23                          => "21"
      case 24                          => "21"
      case i: Int if i >= 10 && i <= 12 => "10-11"
      case i: Int if i >= 14 && i <= 21 => "14-20"
    }
  }

  def coeffs(count: Int, map: Map[String, Double], countOmon:Int): (Map[String, Double], Map[String, Double]) = {
    val kef = countOmon match {
      case 2 => {
        val oneOmon = map.get(TournamentResult.OMON_1.descr).getOrElse(0.0) * 100
        val twoOmon = map.get(TournamentResult.OMON_2.descr).getOrElse(0.0) * 100
        val citWin = map.get(TournamentResult.GOROD_WIN.descr).getOrElse(0.0) * 100
        val x = 100 / (citWin * 1 + oneOmon * 2 / 3 + twoOmon / 3)
        val mapCit = new collection.mutable.HashMap[String, Double]()
        mapCit += (TournamentResult.GOROD_WIN.descr -> StatUtils.roundDouble(x))
        mapCit += (TournamentResult.OMON_1.descr -> StatUtils.roundDouble(x * 2 / 3))
        mapCit += (TournamentResult.OMON_2.descr -> StatUtils.roundDouble(x / 3))
        mapCit += (TournamentResult.MAFIA_WIN.descr -> StatUtils.roundDouble(0.0))

        val oneOmonMaf = map.get(TournamentResult.OMON_1.descr).getOrElse(0.0) * 100
        val twoOmonMaf = map.get(TournamentResult.OMON_2.descr).getOrElse(0.0) * 100
        val mafWin = map.get(TournamentResult.MAFIA_WIN.descr).getOrElse(0.0) * 100
        val y = 100 / (mafWin * 1 + oneOmonMaf / 3 + twoOmonMaf * 2 / 3)
        val mapMaf = new collection.mutable.HashMap[String, Double]()
        mapMaf += (TournamentResult.GOROD_WIN.descr -> 0.0)
        mapMaf += (TournamentResult.OMON_1.descr -> StatUtils.roundDouble(y / 3))
        mapMaf += (TournamentResult.OMON_2.descr -> StatUtils.roundDouble(y * 2 / 3))
        mapMaf += (TournamentResult.MAFIA_WIN.descr -> StatUtils.roundDouble(y))
        (mapCit.toMap, mapMaf.toMap)
      }
      case 3 => {
        val oneOmon = map.get(TournamentResult.OMON_1.descr).getOrElse(0.0) * 100
        val twoOmon = map.get(TournamentResult.OMON_2.descr).getOrElse(0.0) * 100
        val threeOmon = map.get(TournamentResult.OMON_3.descr).getOrElse(0.0) * 100
        val citWin = map.get(TournamentResult.GOROD_WIN.descr).getOrElse(0.0) * 100
        val x = 100 / (citWin * 1 + oneOmon * 3 / 4 + twoOmon / 2 + threeOmon / 4)
        val mapCit = new collection.mutable.HashMap[String, Double]()
        mapCit += (TournamentResult.GOROD_WIN.descr -> StatUtils.roundDouble(x))
        mapCit += (TournamentResult.OMON_1.descr -> StatUtils.roundDouble(x * 3 / 4))
        mapCit += (TournamentResult.OMON_2.descr -> StatUtils.roundDouble(x / 2))
        mapCit += (TournamentResult.OMON_3.descr -> StatUtils.roundDouble(x / 4))
        mapCit += (TournamentResult.MAFIA_WIN.descr -> 0.0)

        val oneOmonMaf = map.get(TournamentResult.OMON_1.descr).getOrElse(0.0) * 100
        val twoOmonMaf = map.get(TournamentResult.OMON_2.descr).getOrElse(0.0) * 100
        val threeOmonMaf = map.get(TournamentResult.OMON_3.descr).getOrElse(0.0) * 100
        val mafWin = map.get(TournamentResult.MAFIA_WIN.descr).getOrElse(0.0) * 100
        val y = 100 / (mafWin * 1 + oneOmonMaf / 4 + twoOmonMaf / 2 + threeOmonMaf * 3 / 4)
        val mapMaf = new collection.mutable.HashMap[String, Double]()
        mapMaf += (TournamentResult.GOROD_WIN.descr -> 0.0)
        mapMaf += (TournamentResult.OMON_1.descr -> StatUtils.roundDouble(y / 4))
        mapMaf += (TournamentResult.OMON_2.descr -> StatUtils.roundDouble(y / 2))
        mapMaf += (TournamentResult.OMON_3.descr -> StatUtils.roundDouble(y * 3 / 4))
        mapMaf += (TournamentResult.MAFIA_WIN.descr -> StatUtils.roundDouble(y))
        (mapCit.toMap, mapMaf.toMap)
      }
      case 4 => {
        val oneOmon = map.get(TournamentResult.OMON_1.descr).getOrElse(0.0) * 100
        val twoOmon = map.get(TournamentResult.OMON_2.descr).getOrElse(0.0) * 100
        val threeOmon = map.get(TournamentResult.OMON_3.descr).getOrElse(0.0) * 100
        val fourOmon = map.get(TournamentResult.OMON_4.descr).getOrElse(0.0) * 100
        val citWin = map.get(TournamentResult.GOROD_WIN.descr).getOrElse(0.0) * 100
        val x = 100 / (citWin * 1 + oneOmon * 4 / 5 + twoOmon * 3 / 5 + threeOmon * 2 / 5 + fourOmon / 5)
        val mapCit = new collection.mutable.HashMap[String, Double]()
        mapCit += (TournamentResult.GOROD_WIN.descr -> StatUtils.roundDouble(x))
        mapCit += (TournamentResult.OMON_1.descr -> StatUtils.roundDouble(x * 4 / 5))
        mapCit += (TournamentResult.OMON_2.descr -> StatUtils.roundDouble(x * 3 / 5))
        mapCit += (TournamentResult.OMON_3.descr -> StatUtils.roundDouble(x * 2 / 5))
        mapCit += (TournamentResult.OMON_4.descr -> StatUtils.roundDouble(x / 5))
        mapCit += (TournamentResult.MAFIA_WIN.descr -> 0.0)

        val oneOmonMaf = map.get(TournamentResult.OMON_1.descr).getOrElse(0.0) * 100
        val twoOmonMaf = map.get(TournamentResult.OMON_2.descr).getOrElse(0.0) * 100
        val threeOmonMaf = map.get(TournamentResult.OMON_3.descr).getOrElse(0.0) * 100
        val fourOmonMaf = map.get(TournamentResult.OMON_4.descr).getOrElse(0.0) * 100
        val mafWin = map.get(TournamentResult.MAFIA_WIN.descr).getOrElse(0.0) * 100
        val y = 100 / (mafWin * 1 + oneOmonMaf / 5 + twoOmonMaf * 2 / 5 + threeOmonMaf * 3 / 5 + fourOmonMaf * 4 / 5)
        val mapMaf = new collection.mutable.HashMap[String, Double]()
        mapMaf += (TournamentResult.GOROD_WIN.descr -> 0.0)
        mapMaf += (TournamentResult.OMON_1.descr -> StatUtils.roundDouble(y / 5))
        mapMaf += (TournamentResult.OMON_2.descr -> StatUtils.roundDouble(y * 2 / 5))
        mapMaf += (TournamentResult.OMON_3.descr -> StatUtils.roundDouble(y * 3 / 5))
        mapMaf += (TournamentResult.OMON_4.descr -> StatUtils.roundDouble(y * 4 / 5))
        mapMaf += (TournamentResult.MAFIA_WIN.descr -> StatUtils.roundDouble(y))
        (mapCit.toMap, mapMaf.toMap)
      }
    }
    kef
  }

  def getMaximumOmonCount(name:String) = {
    name match {
      case "7"                            => 2
      case "8"                            => 2
      case "9"                            => 2
      case "12"                         => 3
      case "13"                          => 3
      case "21"                          => 4
      case "10-11" => 2
      case "14-20" => 3
    }
  }
  
  def groupKefs(games: List[(Int, String, String)]) = {
    val groupedByPlayersSize = games.groupBy(groupKrest).map { case (k, v) => k -> v.map(_._3) }
    val groupedByResult = groupedByPlayersSize.map(e => e._1 -> e._2.groupBy(identity)).map(e => e._1 -> e._2.map(g => g._1 -> g._2.size))
    val groupedByResultAndCount = groupedByResult.map(e => {
      e._1 -> {
        val count = e._2.map(_._2).sum
        val gr = e._2.map(f => f._1 -> StatUtils.roundDouble(f._2.toDouble / count))
        (count, gr)
      }
    })

    val groupedKefs = groupedByResultAndCount.map(g => {
      val name = g._1
      val stat = g._2
      val count = stat._1
      val map = stat._2
      val countOmon = getMaximumOmonCount(name)
      val (cit, maf) = coeffs(count, map, countOmon)
      name -> (cit, maf)
    })
    groupedKefs
  }

  def calculate(games: List[(String, String, String)], groupedKefs: Map[String, (Map[String, Double], Map[String, Double])], delim:Int) = {
    val playersMap = new collection.mutable.HashMap[String, Stat]()
    games.foreach(g => {
      val countPlayers = g._1
      val result = g._3
      val players = parsePlayers(g._2)
      players.foreach(p => {
        val stat = playersMap.getOrElse(p._1, new Stat(p._1))
        val count = stat.count + 1
        val isMaf = p._2.role == Role.BOSS.role || p._2.role == Role.MAFIA.role
        val kefs = groupedKefs.get(countPlayers).get
        val pointsMap = if (isMaf) kefs._2 else kefs._1
        val earned = pointsMap.get(result).get
        val possible = getPossiblePoints(isMaf, pointsMap)
        val points = stat.points + earned
        val possiblePoints = stat.possiblePoints + possible
        val kpd = points / possiblePoints
        playersMap.update(p._1, new Stat(p._1, count, points, possiblePoints, kpd))
      })
    })
    val top = playersMap.filter(_._2.count > delim).map(_._2).toSeq.sortBy(_.kpd).reverse
    top.zipWithIndex.map(e => (e._2, e._1.name, e._1.count, e._1.kpd))
  }
  case class Stat(name: String, count: Int = 0, points: Double = 0.0, possiblePoints: Double = 0.0, kpd: Double = 0.0)

  def printKef(location: Location, item: (String, (Map[String, Double], Map[String, Double]))) = {
    println(s"[spoiler=Статистика ${location.name} игры на ${item._1} человек]")

    println(s"[table]")
    println(s"[tbody]")
    print(s"[tr]")
    item._2._1.foreach(res => {
      print(s"[th]${res._1}[/th]")
    })
    print(s"[/tr]")
    println()
    println(s"[tr][th]Победа честных[/th][/tr]")
    print(s"[tr]")
    item._2._1.foreach(res => {
      print(s"[td]${res._2}[/td]")
    })
    print(s"[/tr]")
    println()
    println(s"[tr][th]Победа мафии[/th][/tr]")
    print(s"[tr]")
    item._2._2.foreach(res => {
      print(s"[td]${res._2}[/td]")
    })
    print(s"[/tr]")
    println()
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")
  }

  def calculateLocation(location: Location) = {
    val playersMap = new collection.mutable.HashMap[String, Seq[(Int, String, Int, Double)]]()

    val games = DB().loadforLocation(2017, location, 3) //data in format countPlayers, players, result
    val gamesTransformed = games.map(e => (groupSize(e._1), e._2, e._3))
    val groupedKefs = groupKefs(games)
    //groupedKefs.foreach(printKef(location, _))
    val groupedByPlayersSize = games.groupBy(groupKrest).map { case (k, v) => k -> v.map(f => (f._2, f._3)) }
    val counts = groupedByPlayersSize.map({ case (k, v) => k -> v.size }).filter(_._2 > 200)
    val overall = calculate(gamesTransformed, groupedKefs, 30)
    playersMap.put("Общий рейтинг", overall)
    groupedByPlayersSize.foreach(g => {
      val ok = counts.contains(g._1)
      ok match {
        case true => {
          val filtered = g._2.map(f => (g._1, f._1, f._2))
          val res = calculate(filtered, groupedKefs, 30)
          playersMap.put(g._1, res)
        }
        case false => ()
      }
    })
    playersMap
  }

  def ratingForPlayer(player: String, location: Location) = {
    val res = calculateLocation(location)
    res.map(r => {
      val prin = r._2.find(_._2 == player).getOrElse(("Не попал в рейтинг", "", "-", "-"))
      (location.name, r._1, prin._1.toString, prin._3.toString, prin._4.toString)
    })
  }

  def main(args: Array[String]) = {
    /*val player = "Aspiring"
    val res = ratingForPlayer(player, Location.KRESTY) ++ ratingForPlayer(player, Location.OZHA)
    println(s"[spoiler=Статистика игрока ${player}]")
    println(s"[table]")
    println(s"[tbody]")
    println(s"[tr][th]Улица[/th][th]Количество игроков в партии[/th][th]Позиция в рейтинге[/th][th]Число игр[/th][th]КПД[/th][/tr]")
    res.foreach(r => {
      println(s"[tr][td]${r._1}[/td][td]${r._2}[/td][td]${r._3}[/td][td]${r._4}[/td][td]${r._5}[/td][/tr]")
    })
    println(s"[/tbody]")
    println(s"[/table]")
    println(s"[/spoiler]")*/
    val calc = calculateLocation(Location.KRESTY).get("Общий рейтинг").get
    printRating(Location.KRESTY,("Общий рейтинг", calc))
    val calc2 = calculateLocation(Location.OZHA).get("Общий рейтинг").get
    printRating(Location.OZHA,("Общий рейтинг", calc2))
  }
}