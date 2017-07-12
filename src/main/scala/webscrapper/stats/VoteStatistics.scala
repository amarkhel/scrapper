package webscrapper.stats

import scala.collection.mutable.ListBuffer
import webscrapper.Player
import webscrapper.Round
import scala.collection.mutable
import webscrapper.RoundType
import webscrapper.util.StatUtils._
import webscrapper.Role

class VoteStatistics(val players: List[Player], val rounds: List[Round]) {
  type Map[T, E] = mutable.HashMap[T, E]
  val playerRounds = new Map[Player, List[Round]]
  val votedRounds = new Map[Player, List[Round]]
  val citizenRounds = new Map[Player, List[Round]]
  val citizenVotedRounds = new Map[Player, List[Round]]
  val roundsCount = new Map[Player, Int]
  val votedRoundsCount = new Map[Player, Int]
  val citizenRoundsCount = new Map[Player, Int]
  val citizenVotedRoundsCount = new Map[Player, Int]
  players.foreach(populate)

  private def populate(player: Player) = {
    val roundsPlayed = filterRounds(rounds, (r: Round) => r.alived(player))
    playerRounds.put(player, roundsPlayed)
    roundsCount.put(player, roundsPlayed.size)

    val roundsVotedPlayed = filterRounds(roundsPlayed, (r: Round) => r.hasVoteFrom(player))
    votedRounds.put(player, roundsVotedPlayed);
    votedRoundsCount.put(player, roundsVotedPlayed.size)

    val citRoundsPlayed = filterRounds(roundsPlayed, (r: Round) => r.typeIs(RoundType.CITIZEN) && !r.inMoroz(player))
    citizenRounds.put(player, citRoundsPlayed);
    citizenRoundsCount.put(player, citRoundsPlayed.size)

    val citVotedRounds = filterRounds(roundsVotedPlayed, (r: Round) => r.typeIs(RoundType.CITIZEN) && !r.inMoroz(player))
    citizenVotedRounds.put(player, citVotedRounds);
    citizenVotedRoundsCount.put(player, citVotedRounds.size)
  }

  private def filterRounds(rounds: List[Round], predicate: Round => Boolean) = rounds.filter(predicate).toList

  /**
   * @return list of players who did maximum votes during the day along
   *  with maximum count of votes from them
   */
  def maxCitizenVotesFrom = max(citizenVotedRoundsCount.toMap)

  /**
   * @return list of players who did minimum votes during the day along
   * with minimum count of votes from them
   */
  def minCitizenVotesFrom = min(citizenVotedRoundsCount.toMap)

  /**
   * @return list of players who have ability to make maximum votes during
   * the day along with maximum count of votes from them
   */
  def maxPossibleCitizenVotesFrom = max(citizenRoundsCount.toMap)

  /**
   * @return list of players who have ability to make minimum votes during
   * the day along with minimum count of votes from them
   */
  def minPossibleCitizenVotesFrom = min(citizenRoundsCount.toMap)

  /**
   * @param player - this player will be analyzed
   * @param role - that we want to know statistics about
   * @return count of votes from particular player to given role
   */
  def countCitizenVotesTo(player: Player, role: Role) = citizenVotedRounds(player).filter(_.hasVoteFromToRole(player, role)).size

  def countCitizenVotesToMafia(player: Player) = countCitizenVotesTo(player, Role.MAFIA)

  def countOtvetka(player: Player) = {
    val rounds = citizenVotedRounds.get(player)
    rounds match {
      case None => 0
      case Some(list) => {
        list.map(f => {
          f.hasOtvetka(player)
        }).filter(_ == true).size
      }
    }

  }

  /**
   * @param player - this player will be analyzed
   * @return count of votes from particular player to any role
   */
  def countCitizenVotesFrom(player: Player) = citizenVotedRoundsCount(player)

  /**
   * @param player - this player will be analyzed
   * @return count of possible votes from particular player to any role
   */
  def countPossibleCitizenVotesFrom(player: Player) = citizenRounds(player).filter(!_.inMoroz(player)).size

  /**
   * @param player - this player will be analyzed
   * @return count of votes that was made for that particular
   * player from other players
   */
  def countVotesForPlayer(player: Player) = citizenRounds(player).map(_.countVotesFor(player)).sum

  def activityLevel(player: Player) = roundDouble(1.0 * countCitizenVotesFrom(player) / countPossibleCitizenVotesFrom(player))
}