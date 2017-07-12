package webscrapper

sealed trait Location{def name:String}

object Location extends Serializable {
  
  case object KRESTY extends Location{val name="Улица Крещения"}
  case object OZHA extends Location{val name="Улица Ожидания"}
  case object SUMRAK extends Location{val name="Сумеречный переулок"}
  
  val values:List[Location] = List(KRESTY, OZHA, SUMRAK)
  
  def get(name:String):Location = {
    require(name != null)
    values.find(name contains _.name).getOrElse(throw new IllegalArgumentException(s"Location $name not found"))
  }
}