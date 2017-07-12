package webscrapper.service

import webscrapper.Location
import java.time.LocalDateTime
import webscrapper.Player
import webscrapper.ChatMessage
import webscrapper.Role
import webscrapper.Vote


case class GameSnapshot(id:Long, loc:Location, start:LocalDateTime, countRounds:Int, alived:List[Player], prisoned:List[Player], timeouted:List[Player], killed:List[Player], chat:List[ChatMessage], order:Int, votes:List[(Int, List[Vote])], finished:Boolean) {
  
  
  
}

