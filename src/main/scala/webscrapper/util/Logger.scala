package webscrapper.util

import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.StandardCharsets

trait Logger {
  
  val path = Paths.get("output.txt" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now));
  val charset = StandardCharsets.UTF_8;

  def log(value:String) = {
    println(value)
    Files.write(path, value.getBytes(StandardCharsets.UTF_8), APPEND, CREATE)
  }
  
  def info(value:String) = {
    println(value)
    Files.write(path, value.getBytes(StandardCharsets.UTF_8), APPEND, CREATE)
    Some(value)
  }
}