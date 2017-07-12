package webscrapper

import java.nio.file.Paths

import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.mutable.HashMap
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import webscrapper._

class PointRule(val result: String, val role: String, val count: Int, val isTimeout: Boolean, val maniacHasKills: Boolean, val points: Double, val formula: String)

object PointRule {

  val pointRules = new HashMap[String, List[PointRule]]()
  
  def get(file: String) = pointRules.getOrElseUpdate(file, load(file))
  
  def load(file: String) = {
    val wb = loadWorksheet(file)
    val start = findFirst(wb)
    val sheet = wb.getSheet(start._1)
    val startRow = start._2
    val startColumn = start._3
    val endRow = findLast(wb, start._1, startRow, startColumn)
    val rules = for (i <- startRow to endRow) yield makeRule(sheet.getRow(i), startColumn)
    rules.toList
  }

  def findMaximumPossiblePoints(game: Game, name:String)(implicit tournamentName:TournamentName) = {
    val player = game.findPlayer(name).get
    val rules = pointRules.getOrElseUpdate(tournamentName.name, load(tournamentName.name))
    val count = game.players.size
    val points = player.isManiac match {
      case true => player.points
      case false => {
        val result = if (player.isMafia) "Мафия победила" else "Честные победили"
        val roleDescr = if (player.isMafia) "Мафия" else "Честные"
        rules
          .filter(_.result == result)
          .filter(_.count == count)
          .filter(_.role == roleDescr)
          .find(_.isTimeout == false)
          .get
          .points
      }
    }
    points
  }

  private def makeRule(implicit row: Row, startColumn: Int) = {
    val role = parseRole(column(0))
    val count = parseInt(column(1))
    val result = parseResult(column(2))
    val isTimeout = parseBoolean(column(3))
    val maniacHasKills = parseBoolean(column(4))
    val points = parsePoints(column(5))
    new PointRule(result, role, count, isTimeout, maniacHasKills, points._1, points._2)
  }

  private def column(index: Int)(implicit row: Row, column: Int) = cellValue(row, column + index).getOrElse("")

  private def cellValue(row: Row, column: Int) = {
    val content = row.getCell(column, RETURN_BLANK_AS_NULL)
    if (content != null) Some(content.getStringCellValue.trim) else None
  }

  private def parseRole(str: String) = findRole(str)

  private def parseResult(str: String) = findResult(str)

  private def parsePoints(str: String) = {
    Try(str.toDouble) match {
      case Success(d) => (d, "")
      case Failure(ex) => (-1.0, str)
    }
  }
  
  private def parseInt(str: String) = {
    Try(str.toInt) match {
      case Success(d) => d
      case Failure(ex) => -1
    }
  }
  
  private def parseBoolean(str: String) = {
    Try(str.toBoolean) match {
      case Success(d) => d
      case Failure(ex) => false
    }
  }

  private def loadWorksheet(file: String) = {
    val path = Paths.get(Properties.path + file + "/points.xls").toFile
    WorkbookFactory.create(path)
  }

  private def findFirst(wb: Workbook) = {
    val temp = for {
      sheet <- wb
      row <- sheet
      cell <- row
      if (cell.getCellType == Cell.CELL_TYPE_STRING)
      if (cell.getRichStringCellValue.getString == "Роль")
    } yield (sheet.getSheetName, row.getRowNum, cell.getColumnIndex)
    val first = temp.toList(0)
    (first._1, first._2 + 1, first._3)
  }
  
  private def findLast(wb: Workbook, sheetName:String, row:Int, column:Int) = {
    val sheet = wb.getSheet(sheetName)
    val list = for {
      row <- sheet
      if (row.getCell(column) == null)
    } yield row.getRowNum
    list.toList(0)
  }

  private def findRole(role: String) = {
    role match {
      case "player.isPositive()" => "Честные"
      case "player.isMafia()" => "Мафия"
      case "player.isManiac()" => "Маньяк"
      case _ => ""
    }
  }

  private def findResult(result: String) = {
    result match {
      case "game.isGorodWin()" => "Честные победили"
      case "game.isMafiaWin()" => "Мафия победила"
      case "game.singleOmon()" => "Одинарный омон"
      case "game.doubleOmon()" => "Двойной омон"
      case "game.tripleOmon()" => "Тройной омон" 
      case _ => ""
    }
  }
}