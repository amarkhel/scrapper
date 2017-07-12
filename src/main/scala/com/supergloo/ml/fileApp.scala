package com.supergloo.ml

import java.io.File
import scala.util.Try

object fileApp extends App {
  val dir = new File("C:\\Users\\amarkhel\\git\\youtrack-to-jira\\restdata")
  dir.listFiles.filter(_.isFile).toList.foreach { file =>
        if(file.getName.endsWith(".xml")) mv(file.getName, file.getName.replaceAll("", "")) else println()
    }

def mv(oldName: String, newName: String) = 
  Try(new File(oldName).renameTo(new File(newName))).getOrElse(false)
}