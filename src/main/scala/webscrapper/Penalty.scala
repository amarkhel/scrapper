package webscrapper

case class Penalty(val gameId:Long, descr:String, player:User, team:Team, points:Double){
  require(gameId > 0 && !descr.isEmpty && team != null && points >=0)
}