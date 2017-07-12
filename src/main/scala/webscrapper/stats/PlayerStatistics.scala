package webscrapper.stats

import webscrapper.Player
import scala.collection.mutable
import webscrapper.Achievement
import webscrapper.util.StatUtils._

class PlayerStatistics (val players:List[Player]){
	
  
	val playersRating = players.map { p => p -> roundDouble(p.achievementScore)}.toMap
	
	/**
	 * @return best player of the game
	 */
	def best = players.find{_.achievementScore == players.map(_.achievementScore).max}.get
	
	override def toString = {
		val builder = new StringBuilder(1024)
		builder.append("Статистика игроков: \n")
		players.foreach {(p:Player) => builder.append("\t" + p.name + " : " + p.achievementScore + "\n");
		  p.achievements.foreach {(a:Achievement) => builder.append("\t\t" + a.description + " : " + a.ratingPoints + "\n") }  
		}
		builder.append(players.map(_.achievementScore).max)
		builder.toString
	}
}