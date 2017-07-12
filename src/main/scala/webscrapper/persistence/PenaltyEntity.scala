package webscrapper.persistence

import webscrapper.Team

case class PenaltyEntity(val gameId:Long, val descr:String, val player:PlayerEntity, val team:Team,val points:Double)