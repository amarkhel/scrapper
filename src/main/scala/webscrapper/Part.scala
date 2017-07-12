package webscrapper

import webscrapper.util.StatUtils

case class Part(val name: String, val games: List[Game], val tournament:String) {

  require(!name.isEmpty && games != null && games.size > 0)

  def resultDistribution = {
    val map = possibleResults.map { a => a -> games.filter { _.tournamentResult == a }.size }.toMap
    toJson(map)
  }
  
  def possibleResults = TournamentResult.possibleResults(tournament)

  private def toJson(results: Map[TournamentResult, Int]) = {
    val list = results map { case (key, value) => "['" + key.descr + "'," + StatUtils.roundDouble(value.toDouble / games.size * 100) + "]" }
    "[" + list.mkString(",") + "]"
  }
}