package webscrapper

case class Team (val name:String, players:List[User]){
  require(!name.isEmpty && !players.isEmpty)
  def containsPlayer(name:String) = {
    require(name != null)
    players.count {_.name == name} > 0
  }
}