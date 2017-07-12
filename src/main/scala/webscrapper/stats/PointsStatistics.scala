package webscrapper.stats

import webscrapper.Player

class PointsStatistics (val players:List[Player]) {
  val playerPoints:Map[Player, Double] = players.map(p => p -> p.points).toMap
	
	/**
	 * @param player
	 * @return points for this player
	 */
	def getPoints(player:Player) = playerPoints(player)
	
	override def toString = players.map(p => p.name + "(" + p.points + ")").mkString(",")
}