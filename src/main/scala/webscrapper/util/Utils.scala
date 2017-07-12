package webscrapper.util

import webscrapper.Player

object Utils {
   def first(players: List[Player]) : Option[Player] = if (players.isEmpty) None else Some(players(0))
   
   def equal(player: Option[Player], compared: Option[Player]) = player.isDefined && compared.isDefined && compared == player
}