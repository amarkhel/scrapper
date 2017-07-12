package object webscrapper {
  type MutableMap[T, E] = collection.mutable.HashMap[T, E]
  implicit def optionToPlayer(player:Player) = Option(player)
  implicit val tournamentName:TournamentName = TournamentName("Default")
  case class TournamentName(val name:String)
}