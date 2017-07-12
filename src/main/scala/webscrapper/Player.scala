package webscrapper

import scala.collection.mutable.ListBuffer
import Role._

class Player(val role: Role, var status: FinishStatus, val name: String) extends Serializable {
  require(!name.isEmpty)
  
  val achievements = ListBuffer[Achievement]()
  private var _points = 0.0
  def points = _points
  def points(value: Double) = _points = value
  private var _sex = ""
  def sex = _sex
  def sex(value: String) = _sex = value
  private var _avatar = ""
  def avatar = _avatar
  def avatar(value: String) = _avatar = value
  
  def roleName = role.role
  def addAchievement(ach: Achievement) = achievements += ach
  def achievementScore = achievements.map(_.ratingPoints).sum
  
  def isMafia = inRoles(List(BOSS, MAFIA))
  def isKomissar = inRoles(List(KOMISSAR, SERZHANT))
  def isCitizen = inRoles(List(CHILD, CHILD_GIRL, CHILD_UNKNOWN, CITIZEN))
  def isChild = inRoles(List(CHILD, CHILD_GIRL, CHILD_UNKNOWN))
  def isCitizenRaw = isInRole(CITIZEN)
  def isPositive = !isNegative
  def isNegative = isMafia || isManiac
  def isRole = !isInRole(CITIZEN)
  def canBeKilledByMafia = !isMafia && !isChild
  def isDoctor = isInRole(DOCTOR)
  def isManiac = isInRole(MANIAC)
  def isBoss = isInRole(BOSS)
  def isMainKomissar = isInRole(KOMISSAR)
  def isSerzhant = isInRole(SERZHANT)
  def isImportantGoodRole = inRoles(List(KOMISSAR, SERZHANT, DOCTOR))
  //def isInRole(rolePredicate: => Boolean) = rolePredicate

  override def equals(other: Any): Boolean =
    other match {
      case that: Player => (that canEqual this) && name == that.name
      case _ => false
    }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Player]

  override def hashCode: Int = 41 * (41 + name.hashCode())
  
  private def inRoles(roles: List[Role]) = roles contains role
  
  private[webscrapper] def isInRole(role: Role) = inRoles(List(role))
  
  override def toString = s"name = $name, role = $role"
}