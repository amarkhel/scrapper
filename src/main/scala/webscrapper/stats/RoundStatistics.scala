package webscrapper.stats

import webscrapper.Round
import webscrapper.Player
import webscrapper.RoundType

class RoundStatistics(val rounds: List[Round]) {

  def killed(player: Player) = conditionTrue(_.wasKilled(player))

  def checked(player: Player) = conditionTrue(_.checked(player))

  def prisoned(player: Player) = conditionTrue(_.isPrisoned(player))

  def prisonedFirst(player: Player) = first(RoundType.CITIZEN, _.hasPrisoners).map(_.isPrisoned(player)).getOrElse(false)

  def checkedFirst(player: Player) = first(RoundType.KOMISSAR, _.hasChecks).map(_.checked(player)).getOrElse(false)

  def killedFirst(player: Player) = first(RoundType.MAFIA, _.hasKills).map(_.wasKilled(player)).getOrElse(false)

  private def conditionTrue(condition: Round => Boolean) = {
    rounds.filter(condition).size > 0
  }
  
  private def first(roundType: RoundType, predicate: Round => Boolean) = rounds.sortBy(_.order).filter(_.typeIs(roundType)).find(predicate)
}