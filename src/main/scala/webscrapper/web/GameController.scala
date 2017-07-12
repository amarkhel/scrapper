package webscrapper.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import scala.collection.convert.WrapAsJava

import webscrapper.Game
import webscrapper.Player
import webscrapper.Round
import webscrapper.Tournament
import webscrapper.service.GameService
import webscrapper.service.GameSnapshot
import webscrapper.GameSimulation
import webscrapper.Role
import webscrapper.Location
import webscrapper.TournamentResult

@Controller
class GameController {
  val gameService = new GameService

  val cache = new collection.mutable.HashMap[Long, GameSimulation]()
  
  lazy val playerCache = gameService.loadAllPlayers()
  /*@RequestMapping(value = Array("/clear/{name}"), method = Array(RequestMethod.GET))
  def clear(@PathVariable(value = "name") name: String) = {
    gameService.cleanup(name)
    "index";
  }*/
  
  @RequestMapping(value = Array("/simulation/{id}/{order}"), method = Array(RequestMethod.GET))
  def simulation(@PathVariable(value = "id") id: Long, @PathVariable(value = "order") order: Int,
      @RequestParam(value = "prev", required = false) prev: String, model: Model) = {
    val simulated = if(cache.get(id).isDefined) cache.get(id).get else {
      val res = gameService.simulate(id, order).get
      if(res.finished) {cache.put(id, res)}
      res
    }
    model.addAttribute("simulation", simulated)
    model.addAttribute("order", order)
    model.addAttribute("converter", new Converter)
    if(prev != null) {
       model.addAttribute("prev", prev)
    } else {
      model.addAttribute("prev", if(order > 2) (order - 2) else 1)
    }
   
    "simulation";
  }
  
  @RequestMapping(value = Array("/simulation/{id}"), method = Array(RequestMethod.GET))
  def simulation(@PathVariable(value = "id") id: Long,  model: Model) = {
    val simulated = gameService.simulate(id, 1)
    model.addAttribute("simulation", simulated.get)
    model.addAttribute("converter", new Converter)
   
    "simulationChooser";
  }
  
  @RequestMapping(value = Array("/simulation"), method = Array(RequestMethod.GET))
  def simulation() = {
    "simulationChooser";
  }
  
  @RequestMapping(value = Array("/searchGame"), method = Array(RequestMethod.GET))
  def searchGame(@RequestParam(value = "location", required = false) loc: String,
      @RequestParam(value = "result", required = false) res: String,
      @RequestParam(value = "role", required = false) role: String,
      @RequestParam(value="player", required = false) player:String,
      @RequestParam(value = "players", required = false) pl: String,
      @RequestParam(value = "countPl", required = false) countPlayers: String,
      @RequestParam(value = "countR", required = false) countRounds: String,
      @RequestParam(value = "day", required = false) sd: String,
      @RequestParam(value = "month", required = false) sm: String,
      @RequestParam(value = "year", required = false) sy: String,
       model: Model
  ) = {
    val result = if(res != null && !res.isEmpty) res.split(",").map(TournamentResult.byDescription(_)).toList else Nil
    val roles = if(role != null && !role.isEmpty) {
      val rolesAll = role.split(",").toList
      rolesAll.flatMap(toRole)
    } else Nil
    val players = if(pl != null && !pl.isEmpty) pl.split(",").toList else Nil
    val location = if(loc != null && !loc.isEmpty) Location.get(loc) else Location.SUMRAK
    val countPl = if(countPlayers != null && !countPlayers.isEmpty) countPlayers.split(",").map(_.toInt).toList else List(6,30)
    val countR = if(countRounds != null && !countRounds.isEmpty) countRounds.split(",").map(_.toInt).toList else List(0,130)
    val years = if(sy != null) sy.split(",").map(_.toInt).toList else List(2012, 2017)
    val monthes = if(sm != null) sm.split(",").map(_.toInt).toList else List(1, 12)
    val days = if(sd != null) sd.split(",").map(_.toInt).toList else List(1, 31)
    val games = gameService.searchGames(location, result, roles, player, players, countPl(0), countPl(1), countR(0), countR(1), years(0), years(1), monthes(0), monthes(1), days(0), days(1))
    model.addAttribute("games", games)
    model.addAttribute("converter", new Converter)
    "searchResult";
  }
  
  @RequestMapping(value = Array("/search"), method = Array(RequestMethod.GET))
  def search(model: Model) = {
    model.addAttribute("roles", Role.values)
    model.addAttribute("locations", Location.values.filter(_.name != Location.SUMRAK.name))
    model.addAttribute("results", TournamentResult.values)
    model.addAttribute("players", playerCache)
    model.addAttribute("converter", new Converter)
    "search";
  }
  
  @RequestMapping(value = Array("/clearAll"), method = Array(RequestMethod.GET))
  def clearAll = {
    gameService.cleanupAll
    "index";
  }

  @RequestMapping(value = Array("/index"), method = Array(RequestMethod.GET))
  def index = "index"

/*  @RequestMapping(value = Array("/tournament/{name}"), method = Array(RequestMethod.GET))
  def tournament(@PathVariable(value = "name") name: String, model: Model) = {
    val tournament = gameService.createTournament(name)
    model.addAttribute("tournament", tournament)
    model.addAttribute("converter", new Converter)
    "tournament"
  }*/

  /*@RequestMapping(value = Array("/tournamentList"), method = Array(RequestMethod.GET))
  def tournamentList(model: Model) = {
    val names = WrapAsJava.asJavaCollection(gameService.findAllTournaments)
    model.addAttribute("names", names)
    model.addAttribute("converter", new Converter)
    "tournamentList"
  }*/

  @RequestMapping(value = Array("/gameAnalyser"), method = Array(RequestMethod.GET))
  def analyse(@RequestParam(value = "url") url: String, model: Model) = {
    anal(url, model)
  }

  private def anal(url: String, model: Model): String = {
    model.addAttribute("converter", new Converter)
    try {
      val game = gameService.loadGame(url).get
      model.addAttribute("game", game)
      val playerMessages = game.statistics.messageStats.countMessagesForPlayer
      model.addAttribute("playerMessages", toJson(playerMessages.toMap))
      val smileMessages = game.statistics.messageStats.countSmilesForPlayer
      model.addAttribute("smileMessages", toJson(smileMessages.toMap))
      val playersRating = game.playerStats.playersRating
      model.addAttribute("playersRating", toJson(playersRating))
      val roundMessages = game.statistics.messageStats.roundMessages
      model.addAttribute("roundMessages", toJsonRounds(roundMessages.toMap))
      "game"
    } catch {
      case e: Exception => {
        gameService.log(s"Error analyser - $url");
        gameService.log(e.toString);
        model.addAttribute("error", "Номер партии не валиден")
        "index"
      }
    }
  }

  def toJson(map: Map[Player, _]) = {
    val json = for {
      (key, value) <- map
    } yield s"{name:'${key.name}', y:$value}"
    val result = json.mkString(",")
    s"[$result]"
  }

  def toJsonRounds(map: Map[Round, Int]) = {
    val json = for {
      (key, value) <- map
    } yield s"{name:'${key.tpe.descr} - ${key.order}', y:$value}"
    val result = json.mkString(",")
    s"[$result]"
  }
  
  def toRole(role:String) = {
    role match {
        case "Честная Роль" => List(Role.CITIZEN, Role.KOMISSAR, Role.SERZHANT, Role.CHILD, Role.CHILD_GIRL, Role.CHILD_UNKNOWN, Role.DOCTOR, Role.MANIAC)
        case "Мафская Роль" => List(Role.MAFIA, Role.BOSS)
        case "Комская Роль" => List(Role.SERZHANT, Role.KOMISSAR)
        case str => List(Role.get(str))
      }
  }
}