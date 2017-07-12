package com.supergloo

case class Stat(count:Int, minRounds:Int, maxRounds:Int, averageRounds:Double){
  override def toString = s"Количество игр: $count"
}