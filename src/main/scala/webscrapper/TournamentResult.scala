package webscrapper

sealed trait TournamentResult{def descr:String}
object TournamentResult extends Serializable {
  
  case object GOROD_WIN extends TournamentResult{val descr = "Победа города"}
  case object MAFIA_WIN extends TournamentResult{val descr = "Победа мафии"}
  case object OMON_1 extends TournamentResult{val descr = "Одинарный омон"}
  case object OMON_2 extends TournamentResult{val descr = "Двойной омон"}
  case object OMON_3 extends TournamentResult{val descr = "Тройной омон"}
  case object OMON_4 extends TournamentResult{val descr = "Четверной омон"}
  val values:List[TournamentResult] = List(GOROD_WIN, MAFIA_WIN, OMON_1, OMON_2, OMON_3, OMON_4)
  
  def byDescription(descr:String):TournamentResult = {
    require(descr != null) 
    values.find {_.descr == descr}.getOrElse(throw new IllegalArgumentException("Неправильный дескрпишен"))
  }
  
  def possibleResults(tournament:String) = {
    if (tournament == "Семейный кубок 2016") TournamentResult.values.toList
    else TournamentResult.values.toList.takeWhile { res => res != TournamentResult.OMON_3 }
  }
}