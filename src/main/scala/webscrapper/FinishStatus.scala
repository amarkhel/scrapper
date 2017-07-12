package webscrapper

sealed trait FinishStatus{def status:String} 

object FinishStatus extends Serializable {
  case object ALIVE extends FinishStatus{val status = "Выжил"}
  case object KILLED extends FinishStatus{val status = "Убит"}
  case object PRISONED extends FinishStatus{val status = "Посажен в тюрьму"}
  case object TIMEOUT extends FinishStatus{val status = "Вышел по тайму"}
  case object MOROZ extends FinishStatus{val status = "Закончил игру в морозе"}
	
  private val values:List[FinishStatus] = List(ALIVE, KILLED, PRISONED, TIMEOUT, MOROZ)
  def get(status:String):FinishStatus = values.find {_.status == status}.getOrElse(throw new IllegalArgumentException(s"Status $status not found"))
  def map(status:String):FinishStatus = values.find {_.status == status}.getOrElse(ALIVE)
}