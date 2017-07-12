package webscrapper

class PlayerStatisticQueryResult (val players:List[Player], val count:Int) {
  require(players != null && players.size > 0)
  override def toString = players.map(_.name + "(" + count + ")").mkString(",")
}