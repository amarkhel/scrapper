package webscrapper

sealed trait OmonStatus{def countMafia:Int}

object OmonStatus extends Serializable {
  case object ONE extends OmonStatus{val countMafia = 1}
  case object TWO extends OmonStatus{val countMafia = 2}
  case object THREE extends OmonStatus{val countMafia = 3}
  case object FOUR extends OmonStatus{val countMafia = 4}
  private val values:List[OmonStatus] = List(ONE, TWO, THREE, FOUR)
  
  def byCount(count:Int):OmonStatus = {
    require(count >= 0)
    values.find {_.countMafia == count}.getOrElse(throw new IllegalArgumentException(s"Неправильное число омон $count"))
  }
      
}