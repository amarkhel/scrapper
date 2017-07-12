package com.supergloo.ml

import org.apache.spark.sql.SparkSession

object MultiClassifier {
  def main(args: Array[String]): Unit = {
    val name = "весельчакджо"
    implicit val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("TfIdfExample")
      .getOrCreate()
    val perc = GBTApp.evaluate(name).sort("sentence").rdd
    val one = OneRestApp.evaluate(name).sort("sentence").rdd
    val three = RandomForestApp.evaluate(name).sort("sentence").rdd
    val four = NaiveBayesApp.evaluate(name).sort("sentence").rdd
    val five = DesTreeApp.evaluate(name).sort("sentence").rdd
    val six = PerceptronApp.evaluate(name).sort("sentence").rdd
    val res = perc.union(one).union(three).union(four).union(five).union(six)
    val mapped = res.map(f => {
      val lab = if(f.get(0).isInstanceOf[String]) f.getString(0).toDouble else f.getDouble(0)
      val sentence = f.getString(1)
      val predicted = if(f.get(2).isInstanceOf[String]) f.getString(2).toDouble else f.getDouble(2)
      (lab, sentence, predicted, lab == predicted)
    })
    val groupped = mapped.groupBy(_._2)
    val result = groupped.map(f => {
      val s = f._1
      val list = f._2.toList
      val count = list.filter(f => f._1 == f._3).size
      val lab = list(0)._1
      (s, lab, count)
    }).filter(_._1.length() >=100)
    val cit = result.filter(a => a._2 == 1.0 && a._3 > 2).count
    val maf = result.filter(a => a._2 == 0.0 && a._3 > 2).count
    val citWrong = result.filter(a => a._2 == 1.0 && a._3 <= 2).count
    val mafWrong = result.filter(a => a._2 == 0.0 && a._3 <= 2).count
    val mafO = maf + mafWrong
    val citO = cit + citWrong
    val citPercent = cit.toDouble / citO * 100
    val mafPercent = maf.toDouble / mafO * 100
    println("Количество чижей " + citO)
    println("Количество мафов " + mafO)
    println("Количество чижей определенных правильно " + cit)
    println("Количество мафов определенных правильно " + maf)
    println("Процент чижей определенных правильно " + citPercent)
    println("Процент мафов определенных правильно " + mafPercent)
    result.foreach(println)
    /*val filtered = groupped.map(a => a._1 -> (a._2.filter(_._4).size, a._2.head._1)).cache()
    val filt = filtered.filter(f => f._2._1 < 3).count
    val none = filtered.filter(f => f._2._1 == 0).count
    val a1 = filtered.filter(f => f._2._1 == 1).count
    val a2 = filtered.filter(f => f._2._1 == 2).count
    val a3 = filtered.filter(f => f._2._1 == 3).count
    val a4 = filtered.filter(f => f._2._1 == 4).count
    val a5 = filtered.filter(f => f._2._1 == 5).count
    val a6 = filtered.filter(f => f._2._1 == 6).count
    println(filt)
    println(none)
    println(a1)
    println(a2)
    println(a3)
    println(a4)
    println(a5)
    println(a6)*/
    /*val zipped = perc.zip(one).zip(three).zip(four).zip(five).zip(six)
    var first = 0
    var sec = 0
    var third = 0
    var not = 0
    var fs = 0
    var ft = 0
    var st = 0
    var all = 0
    val m = zipped.map(r => {
      val row1 = r._1._1
      val row2 = r._1._2
      val row3 = r._2
      val sentence = row1.getString(1)
      val label = row1.getDouble(0)
      val label2 = row2.getDouble(0)
      val predicted1 = row1.getString(2).toDouble
      val predicted2 = row2.getDouble(2)
      val predicted3 = row3.getDouble(2)
      (label, sentence, predicted1, predicted2, predicted3)
    }).collect()
    val d = m.flatMap(f => {
      if (f._1 != f._3) Some(f._1, f._2, f._3) else None
    })
    d.foreach(println)
    m.foreach(f => {
      val label = f._1
      val predicted1 = f._3
      val predicted2 = f._4
      val predicted3 = f._4
      if(predicted1 == label){
        if(predicted2 == label){
          if(predicted3 == label){
            all +=1
          } else {
            fs+=1
          }
          
        } else {
          if(predicted3 == label){
            ft +=1
          }
          first+=1
        }
      } else if (predicted2 == label) {
        if(predicted3 == label){
            st +=1
         } else {
           sec+=1
         }
          
      } else if (predicted3 == label) {
        third +=1
      } else {
        not +=1
      }
    })
    
    println("first second =" + fs)
    println("first third =" + ft)
    println("second third =" + st)
    println("all =" + all)
    
    println("first=" + first)
    println("sec=" + sec)
    println("thirs=" + third)
    println("not=" + not)
    
    println("percent = " + all.toDouble / m.size * 100)*/
    spark.stop()
  }
}