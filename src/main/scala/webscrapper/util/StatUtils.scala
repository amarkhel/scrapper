package webscrapper.util

import webscrapper.Player
import webscrapper.PlayerStatisticQueryResult
import webscrapper.Round
import scala.collection.mutable.ListBuffer

object StatUtils {
  
  def roundDouble(arg:Double):Double = BigDecimal(arg).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  
  def max(players:Map[Player, Int]) = extract(players, players.values.max)
  
	def min(players:Map[Player, Int]) = extract(players, players.values.min)
	
	def mapToSize[A, B](map:collection.Map[A, List[B]]) = map.map { case (key, value) => (key, value.size)}
	
  def deviation(map:collection.Map[String, Int]) = {
    val count = map.keys.size
    val avg = map.values.sum.toDouble / count
    val squareDev = map.map{ case (key, value) => (value - avg)*(value - avg)}.sum / count
    val pirs  = map.map{ case (key, value) => (value - avg)*(value - avg)/value}.sum
    println("pirson = " + pirs)
    Math.sqrt(squareDev)
  }
  
  def deviation2(map:collection.Map[String, Int]) = {
    
    val count = map.keys.size
    val p = 2.toDouble / (count*2 - 1)
    val pX = 1.toDouble / count 
    val sum = map.values.sum
    val p2 = sum * p
    val pirs  = map.map{ case (key, value) => (value - p2)*(value - p2)/value}.sum
    println("pirson = " + pirs)
    val Mx = map.map{ case (key, value) => value * p}.sum
    val Mx2 = Mx * Mx
    val MxSq = map.map{ case (key, value) => value * value * p}.sum
    val Dx = Math.abs(MxSq - Mx2)
    val S = Math.sqrt(Dx)
    S
  }
  
  def deviation3[A](map:collection.Map[A, Int]) = {
    
    val count = map.keys.size
    val p = 1.toDouble / count
    val sum = map.values.sum
    val p2 = sum * p
    val pirs  = map.map{ case (key, value) => (p2 -value)*(p2 - value)/value}.sum
    println("pirson = " + pirs)
    val Mx = map.map{ case (key, value) => value * p}.sum
    val Mx2 = Mx * Mx
    val MxSq = map.map{ case (key, value) => value * value * p}.sum
    val Dx = Math.abs(MxSq - Mx2)
    val S = Math.sqrt(Dx)
    S
  }
  
	private def filterByValue(players:Map[Player, Int], elem:Int) = players.filter(_._2 == elem).map(_._1).toList
	
	private def extract(playersMap:Map[Player, Int], op: => Int) = {
    require(!playersMap.isEmpty)
    val value = op
		new PlayerStatisticQueryResult(filterByValue(playersMap, value), value)
	}
}