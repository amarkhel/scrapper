package webscrapper

case class Vote(target:Player, destination:Player) {

  require(target != destination)
  
	def from(implicit player:Player) = fromPlayer

	def toMafiaFrom(implicit player:Player) = fromPlayer && destination.isMafia
	
	def toMafia = destination.isMafia
	
	def toImportantGoodRole = destination.isImportantGoodRole

	def toRole(role:Role) = {
	  require(role != null)
	  destination.isInRole(role)
	}

	def toRole = destination.isRole

	def to(player:Player) = {
	  require(player != null)
	  destination == player
	}

	def fromRole(role:Role) = {
	  require(role != null)
	  target.isInRole(role)
	}
	
	def fromMafia = target.isInRole(Role.MAFIA)
	
	def toPlayerFromMafia(player:Player) = to(player) && target.isMafia
	
	def fromPlayerToGoodImportantRole(implicit player:Player) = fromPlayer && destination.isImportantGoodRole
	
	def fromPlayerToRole(implicit player:Player) = fromPlayer && destination.isRole
	
	def fromPlayerToSpecificRole(implicit player:Player, role:Role) = {
	  require(role != null)
	  fromPlayer && destination.isInRole(role)
	}
	
	private def fromPlayer(implicit player:Player) = {
	  //require(player != null)
	  target == player 
	}
	
	private def fromPlayerTo(destination:Player)(implicit player:Player) = {
	  //require(player != null)
	  target == player && destination == destination
	}
}