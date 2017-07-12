package webscrapper

sealed trait Role {
  def role:String
}
object Role extends Serializable {
  case object CITIZEN extends Role{val role="Честный житель"}
  case object MAFIA extends Role{val role="Мафия"}
  case object BOSS extends Role{val role="Босс"}
  case object KOMISSAR extends Role{val role="Комиссар"}
  case object SERZHANT extends Role{val role="Сержант"}
  case object CHILD extends Role{val role="Внебрачный сын босса"}
  case object CHILD_GIRL extends Role{val role="Внебрачная дочь босса"}
  case object CHILD_UNKNOWN extends Role{val role="Внебрачное дитя босса"}
  case object MANIAC extends Role{val role="Маньяк"}
  case object DOCTOR extends Role{val role="Врач"}
  val values:List[Role] = List(Role.CITIZEN, Role.MAFIA, Role.BOSS, Role.KOMISSAR, Role.SERZHANT, Role.CHILD, Role.CHILD_GIRL, Role.MANIAC, Role.DOCTOR, Role.CHILD_UNKNOWN)
  def get(role:String):Role = {
    require(role != null)
    values.find(_.role == role).getOrElse(throw new IllegalArgumentException(s"Role $role not found"))
  }

  def posibleRoles(name: String) = {
    if (name == "Семейный кубок 2016") Role.values.toList
    else List(Role.CHILD, Role.CHILD_GIRL, Role.CITIZEN, Role.KOMISSAR, Role.MAFIA)
  }
  
  def filter(role:Role) = Role.values.filter(_ != role)
  
  def getByRoleName(name:String) = {
    name match {
      case "CITIZEN" => CITIZEN
      case "MAFIA" => MAFIA
      case "BOSS" => BOSS
      case "DOCTOR" => DOCTOR
      case "MANIAC" => MANIAC
      case "KOMISSAR" => KOMISSAR
      case "SERZHANT" => SERZHANT
      case "CHILD" => CHILD
      case "CHILD_GIRL" => CHILD_GIRL
      case "CHILD_UNKNOWN" => CHILD_UNKNOWN
    }
  }
}