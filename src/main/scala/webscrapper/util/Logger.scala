package webscrapper.util

import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

trait Logger {
  
  val file = new File("output.txt")
  val bw = new BufferedWriter(new FileWriter(file))

  def log(value:String) = {
    println(value)
    bw.write(value + "\n");
  }
  
  def info(value:String) = {
    println(value)
    bw.write(value + "\n");
    Some(value)
  }
}