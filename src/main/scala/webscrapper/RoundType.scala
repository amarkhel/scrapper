package webscrapper

sealed trait RoundType{def descr:String}

object RoundType extends Serializable {
  
  case object BOSS extends RoundType{val descr = "Ход босса"}
  case object CITIZEN extends RoundType{val descr = "Ход честных"}
  case object MAFIA extends RoundType{val descr = "Ход мафии"}
  case object KOMISSAR extends RoundType{val descr = "Ход комиссара"}
  case object INITIAL extends RoundType{val descr = "Начальный ход"}
}