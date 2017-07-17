package webscrapper.database

import org.junit.runner.RunWith
import org.scalatest.Matchers
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import webscrapper.Role
import webscrapper.Location
import webscrapper.TournamentResult

@RunWith(classOf[JUnitRunner])
class RepositoryTest extends FunSuite with Matchers {
  
  test("ParsePlayers should work ok"){
    val p = DB().parsePlayers("Любаша(Мафия),mark(Честный житель),Hagman(Комиссар),Гардемарин(Мафия),KlllllKolllll(Честный житель),кошка маша(Честный житель),Эйфория(Честный житель)")
    println(p)
    p.size should be (7)
    p(0)._1 should be ("Любаша")
    p(0)._2 should be (Role.MAFIA)
    p(1)._1 should be ("mark")
    p(1)._2 should be (Role.CITIZEN)
    p(2)._1 should be ("Hagman")
    p(2)._2 should be (Role.KOMISSAR)
    p(3)._1 should be ("Гардемарин")
    p(3)._2 should be (Role.MAFIA)
    p(4)._1 should be ("KlllllKolllll")
    p(4)._2 should be (Role.CITIZEN)
    p(5)._1 should be ("кошка маша")
    p(5)._2 should be (Role.CITIZEN)
    p(6)._1 should be ("Эйфория")
    p(6)._2 should be (Role.CITIZEN)
    val p2 = DB().parsePlayers("nitrogeniu(Врач),Гладиус(Внебрачный сын босса),Барс БКС(Честный житель),Celmz(Честный житель),Йогурт(Честный житель),Brutal(Мафия),Sergey Bragin(Мафия),Rinna(Маньяк),like me(Честный житель),Желчный пузырь(Честный житель),Gogogo(Честный житель),CyMpAK(Сержант),Любит на 99,9(Честный житель),Yurach(Босс),Кот в сапогах(Честный житель),котенок(Честный житель),Namik0(Комиссар)")
    println(p2)
    p2.size should be (17)
    p2(12)._1 should be ("Любит на 99,9")
    p2(12)._2 should be (Role.CITIZEN)
  }
  
  test("isInvalid should work correctly") {
    DB().isInvalid(1L) should be (false)
    DB().isInvalid(2273603) should be (true)
    DB().isInvalid(2815241) should be (false)
  }
  
  test("load should work correctly") {
    val game_1 = DB().exist(1)
    game_1.isRight should be (true)
    game_1.right.get should be (None)
    val game_2273603 = DB().exist(2273603)
    game_2273603.isLeft should be (true)
    game_2273603.left.get should be ("Game is invalid")
    val game_3746727 = DB().exist(3746727)
    game_3746727.isRight should be (true)
    game_3746727.right.get.get should be (true)
  }
  
  test("searchGames should work properly for year and location"){
    val games_2012 = DB().searchGames(location=Location.OZHA, sy=2012, ey=2012)
    games_2012.size should be (8470)
    games_2012.forall(_._2 == Location.OZHA.name) should be (true)
    games_2012.forall(_._7.startsWith("2012")) should be (true)
    val games_2013 = DB().searchGames(location=Location.OZHA, sy=2013, ey=2013)
    games_2013.size should be (4429)
    games_2013.forall(_._2 == Location.OZHA.name) should be (true)
    games_2013.forall(_._7.startsWith("2013")) should be (true)
    val games_2012_2013 = DB().searchGames(location=Location.OZHA, sy=2012, ey=2013)
    games_2012_2013.size should be (12899)
    games_2012_2013.forall(_._2 == Location.OZHA.name) should be (true)
    games_2012_2013.forall(x => x._7.startsWith("2013") || x._7.startsWith("2012")) should be (true)
  }
  
  test("searchGames should work properly for year and location and results"){
    val games_mafia = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.MAFIA_WIN), sy=2012, ey=2012)
    games_mafia.size should be (2252)
    games_mafia.forall(_._2 == Location.OZHA.name) should be (true)
    games_mafia.forall(_._7.startsWith("2012")) should be (true)
    games_mafia.forall(_._3 == TournamentResult.MAFIA_WIN.descr) should be (true)
    val games_omon = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.OMON_1), sy=2012, ey=2012)
    games_omon.size should be (170)
    games_omon.forall(_._2 == Location.OZHA.name) should be (true)
    games_omon.forall(_._7.startsWith("2012")) should be (true)
    games_omon.forall(_._3 == TournamentResult.OMON_1.descr) should be (true)
    val games_both = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.OMON_1, TournamentResult.MAFIA_WIN), sy=2012, ey=2012)
    games_both.size should be (2422)
    games_both.forall(_._2 == Location.OZHA.name) should be (true)
    games_both.forall(_._7.startsWith("2012")) should be (true)
    games_omon.forall(x => x._3 == TournamentResult.OMON_1.descr || x._3 == TournamentResult.MAFIA_WIN.descr) should be (true)
  }
  
  test("searchGames should work properly for year and location and results and playerSize"){
    val games_start = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8)
    games_start.size should be (5849)
    games_start.forall(_._2 == Location.OZHA.name) should be (true)
    games_start.forall(_._7.startsWith("2012")) should be (true)
    games_start.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_start.forall(_._5 >= 8) should be (true)
    val games_end = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, endPl = 15)
    games_end.size should be (1363)
    games_end.forall(_._2 == Location.OZHA.name) should be (true)
    games_end.forall(_._7.startsWith("2012")) should be (true)
    games_end.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_end.forall(_._5 <= 15) should be (true)
    val games_both = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl=10, endPl = 15)
    games_both.size should be (497)
    games_both.forall(_._2 == Location.OZHA.name) should be (true)
    games_both.forall(_._7.startsWith("2012")) should be (true)
    games_both.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_both.forall(x => x._5 <= 15 && x._5 >= 10) should be (true)
  }
  
  test("searchGames should work properly for year and location and results and playerSize with roles but without player"){
    val games = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, roles=List(Role.CITIZEN))
    games.size should be (5849)//it should be same count as without roles, because roles working in conjuction with player
    games.forall(_._2 == Location.OZHA.name) should be (true)
    games.forall(_._7.startsWith("2012")) should be (true)
    games.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games.forall(_._5 >= 8) should be (true)
  }
  
  test("searchGames should work properly for year and location and results and playerSize and rounds size"){
    val games_start = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15)
    games_start.size should be (4170)
    games_start.forall(_._2 == Location.OZHA.name) should be (true)
    games_start.forall(_._7.startsWith("2012")) should be (true)
    games_start.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_start.forall(_._5 >= 8) should be (true)
    games_start.forall(_._6 >= 15) should be (true)
    val games_end = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, endR = 25)
    games_end.size should be (5271)
    games_end.forall(_._2 == Location.OZHA.name) should be (true)
    games_end.forall(_._7.startsWith("2012")) should be (true)
    games_end.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_end.forall(_._5 >= 8) should be (true)
    games_end.forall(_._6 <= 25) should be (true)
    val games_both = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR=15, endR = 25)
    games_both.size should be (3592)
    games_both.forall(_._2 == Location.OZHA.name) should be (true)
    games_both.forall(_._7.startsWith("2012")) should be (true)
    games_both.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_both.forall(_._5 >= 8) should be (true)
    games_both.forall(x => x._6 >= 15 && x._6 <= 25) should be (true)
  }
  
  test("searchGames should work properly for year and location and results and playerSize and rounds size and month"){
    val games_both_month = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8)
    games_both_month.size should be (302)
    games_both_month.forall(_._2 == Location.OZHA.name) should be (true)
    games_both_month.forall(_._7.startsWith("2012-8")) should be (true)
    games_both_month.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_both_month.forall(_._5 >= 8) should be (true)
    games_both_month.forall(_._6 >= 15) should be (true)
    val games_start_month = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8)
    games_start_month.size should be (1338)
    games_start_month.forall(_._2 == Location.OZHA.name) should be (true)
    games_start_month.forall(_._7.startsWith("2012")) should be (true)
    games_start_month.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_start_month.forall(_._5 >= 8) should be (true)
    games_start_month.forall(_._6 >= 15) should be (true)
    val games_end_month = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, em = 8)
    games_end_month.size should be (2556)
    games_end_month.forall(_._2 == Location.OZHA.name) should be (true)
    games_end_month.forall(_._7.startsWith("2012")) should be (true)
    games_end_month.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_end_month.forall(_._5 >= 8) should be (true)
    games_end_month.forall(_._6 >= 15) should be (true)
    val games_month = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, endR=25, startPl = 8, startR = 15, sm=2, em = 8)
    games_month.size should be (2100)
    games_month.forall(_._2 == Location.OZHA.name) should be (true)
    games_month.forall(_._7.startsWith("2012")) should be (true)
    games_month.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_month.forall(_._5 >= 8) should be (true)
    games_month.forall(_._6 >= 15) should be (true)
  }
  
  test("searchGames should work properly for year and location and results and playerSize and rounds size and month and day"){
    val games_both_days = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=15, ed=15)
    games_both_days.size should be (7)
    games_both_days.forall(_._2 == Location.OZHA.name) should be (true)
    games_both_days.forall(_._7.startsWith("2012-8-15")) should be (true)
    games_both_days.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_both_days.forall(_._5 >= 8) should be (true)
    games_both_days.forall(_._6 >= 15) should be (true)
    val games_start_days = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=15)
    games_start_days.size should be (176)
    games_start_days.forall(_._2 == Location.OZHA.name) should be (true)
    games_start_days.forall(_._7.startsWith("2012-")) should be (true)
    games_start_days.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_start_days.forall(_._5 >= 8) should be (true)
    games_start_days.forall(_._6 >= 15) should be (true)
    val games_end_days = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, ed=15)
    games_end_days.size should be (133)
    games_end_days.forall(_._2 == Location.OZHA.name) should be (true)
    games_end_days.forall(_._7.startsWith("2012-")) should be (true)
    games_end_days.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_end_days.forall(_._5 >= 8) should be (true)
    games_end_days.forall(_._6 >= 15) should be (true)
    val games_end_start_days = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15)
    games_end_start_days.size should be (60)
    games_end_start_days.forall(_._2 == Location.OZHA.name) should be (true)
    games_end_start_days.forall(_._7.startsWith("2012-")) should be (true)
    games_end_start_days.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_end_start_days.forall(_._5 >= 8) should be (true)
    games_end_start_days.forall(_._6 >= 15) should be (true)
  }
  
  test("searchGames should work properly for year and location and results and playerSize and rounds size and month and day and player"){
    val games_player = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15, player=Some("Упавшая Звезда"))
    games_player.size should be (15)
    games_player.forall(_._2 == Location.OZHA.name) should be (true)
    games_player.forall(_._7.startsWith("2012-8")) should be (true)
    games_player.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_player.forall(_._5 >= 8) should be (true)
    games_player.forall(_._6 >= 15) should be (true)
    games_player.forall(_._4.contains("Упавшая Звезда")) should be (true)
    
    val games_player_role_cit = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15, player=Some("Упавшая Звезда"), roles=List(Role.CITIZEN))
    games_player_role_cit.size should be (6)
    games_player_role_cit.forall(_._2 == Location.OZHA.name) should be (true)
    games_player_role_cit.forall(_._7.startsWith("2012-8")) should be (true)
    games_player_role_cit.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_player_role_cit.forall(_._5 >= 8) should be (true)
    games_player_role_cit.forall(_._6 >= 15) should be (true)
    games_player_role_cit.forall(_._4.contains("Упавшая Звезда")) should be (true)
    val games_player_role_man = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15, player=Some("Упавшая Звезда"), roles=List(Role.MANIAC))
    games_player_role_man.size should be (1)
    games_player_role_man.forall(_._2 == Location.OZHA.name) should be (true)
    games_player_role_man.forall(_._7.startsWith("2012-8")) should be (true)
    games_player_role_man.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_player_role_man.forall(_._5 >= 8) should be (true)
    games_player_role_man.forall(_._6 >= 15) should be (true)
    games_player_role_man.forall(_._4.contains("Упавшая Звезда")) should be (true)
    val games_player_role_man_cit = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15, player=Some("Упавшая Звезда"), roles=List(Role.MANIAC, Role.CITIZEN))
    games_player_role_man_cit.size should be (7)
    games_player_role_man_cit.forall(_._2 == Location.OZHA.name) should be (true)
    games_player_role_man_cit.forall(_._7.startsWith("2012-8")) should be (true)
    games_player_role_man_cit.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_player_role_man_cit.forall(_._5 >= 8) should be (true)
    games_player_role_man_cit.forall(_._6 >= 15) should be (true)
    games_player_role_man_cit.forall(_._4.contains("Упавшая Звезда")) should be (true)
  }
  
  test("searchGames should work properly for year and location and results and playerSize and rounds size and month and day and player and players"){
    val games_players = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15, player=Some("Упавшая Звезда"), players=List("Celmz"))
    games_players.size should be (8)
    games_players.forall(_._2 == Location.OZHA.name) should be (true)
    games_players.forall(_._7.startsWith("2012-8")) should be (true)
    games_players.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_players.forall(_._5 >= 8) should be (true)
    games_players.forall(_._6 >= 15) should be (true)
    games_players.forall(x => x._4.contains("Упавшая Звезда") && x._4.contains("Celmz")) should be (true)
    
    val games_players_without_player = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15, players=List("Celmz"))
    games_players_without_player.size should be (60)
    games_players_without_player.forall(_._2 == Location.OZHA.name) should be (true)
    games_players_without_player.forall(_._7.startsWith("2012-8")) should be (true)
    games_players_without_player.forall(_._3 == TournamentResult.GOROD_WIN.descr) should be (true)
    games_players_without_player.forall(_._5 >= 8) should be (true)
    games_players_without_player.forall(_._6 >= 15) should be (true)
    
    val games_players_wrong = DB().searchGames(location=Location.OZHA, results=List(TournamentResult.GOROD_WIN), sy=2012, ey=2012, startPl = 8, startR = 15, endR=25, sm = 8, em=8, sd=10, ed=15, player=Some("Упавшая Звезда"), players=List("Cel"))
    games_players_wrong.size should be (0)

  }
  
  test("loadPlayers should work properly for year"){
    val games_players_2012 = DB().loadPlayers(2012)
    games_players_2012.size should be (152104)
    val games_players_2013 = DB().loadPlayers(2013)
    games_players_2013.size should be (100717)
    
  }
  
  test("loadforLocation should work properly for year"){
    val games_players_2012_ozha_1 = DB().loadforLocation(2012, Location.OZHA, 1)
    games_players_2012_ozha_1.size should be (1112)
    val games_players_2012_krest_1 = DB().loadforLocation(2012, Location.KRESTY, 1)
    games_players_2012_krest_1.size should be (16872)
    val games_players_2012_ozha = DB().loadforLocation(2012, Location.OZHA)
    games_players_2012_ozha.size should be (8470)
    val games_players_2012_krest = DB().loadforLocation(2012, Location.KRESTY)
    games_players_2012_krest.size should be (143634)
  }
  
  test("loadAllPlayers should work properly"){
    val games_players_2012_ozha_1 = DB().loadAllPlayers()
    games_players_2012_ozha_1.size should be > 1000
    games_players_2012_ozha_1 should contain("apaapaapa")
    games_players_2012_ozha_1 should contain("Rogue")
    games_players_2012_ozha_1 should contain("Желчный пузырь")
  }
  
  test("loadgames should work properly"){
/*    val games_players_2012 = DB().loadGames(2012)
    games_players_2012.size should be (152104)*/
    /*val games_players_2012_ozha = DB().loadGames(2012, Location.OZHA)
    games_players_2012_ozha.size should be (8470)*/
    val games_players_2012_ozha_puzyr = DB().loadGames(2012, Location.OZHA, "Желчный пузырь")
    games_players_2012_ozha_puzyr.size should be (548)
    val games_players_2012_ozha_puzyr_17 = DB().loadGames(2012, Location.OZHA, "Желчный пузырь", 17)
    games_players_2012_ozha_puzyr_17.size should be (489)
  }
}