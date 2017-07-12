package com.supergloo

import webscrapper.service.GameService
import webscrapper.service.GamesExtractor

object GameExtractorApp {
  def main(args:Array[String]) = {
    val s = new GameService
   
    val missed = for {
        year <- 2012 to 2017
        month <- 1 to 12
        day <- 1 to 31
    } yield GamesExtractor.extractGames(year, month, day).toList
    val m = missed.flatten.toList.filter(id => !s.isInvalid(id.toLong))
    println(m.mkString(","))
  }
}