package com.supergloo.ml

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.classification.BinaryLogisticRegressionSummary
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.functions.max
import org.apache.spark.rdd.RDD.numericRDDToDoubleRDDFunctions
import scala.reflect.runtime.universe

// $example off$

object TFIDFExample {

  def evaluate(name:String)(implicit spark:SparkSession) = {
    import spark.implicits._
    val (training, test) = Util.tfidf2(name)
  val lr = new LogisticRegression()
        .setMaxIter(10)

      // Fit the model
      val lrModel = lr.fit(training)

      // $example on$
      // Extract the summary from the returned LogisticRegressionModel instance trained in the earlier
      // example
      val trainingSummary = lrModel.summary

      // Obtain the objective per iteration.
      val objectiveHistory = trainingSummary.objectiveHistory
      println("objectiveHistory:")
    objectiveHistory.foreach(loss => println(loss))

      // Obtain the metrics useful to judge performance on test data.
      // We cast the summary to a BinaryLogisticRegressionSummary since the problem is a
      // binary classification problem.
      val binarySummary = trainingSummary.asInstanceOf[BinaryLogisticRegressionSummary]

      // Obtain the receiver-operating characteristic as a dataframe and areaUnderROC.
      val roc = binarySummary.roc
      roc.show()
      println(s"areaUnderROC: ${binarySummary.areaUnderROC}")

      // Set the model threshold to maximize F-Measure
      val fMeasure = binarySummary.fMeasureByThreshold
      val maxFMeasure = fMeasure.select(max("F-Measure")).head().getDouble(0)
      val bestThreshold = fMeasure.where($"F-Measure" === maxFMeasure)
        .select("threshold").head().getDouble(0)
      lrModel.setThreshold(bestThreshold)

      // Print the weights and intercept for logistic regression.
      println(s"Weights: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")
      val res = lrModel.evaluate(test)
      val rdd = res.predictions.select("label", "prediction").rdd
      val map = rdd.map(r => r.getDouble(0) -> r.getDouble(1))
      val correct = map.map(d => {
        if (d._1 == d._2) 1 else 0
      })
      println("prediction accuracy = " + correct.sum.toDouble / correct.count * 100 + " for maxIter count = " + 10)
    
val predicted = res.predictions.select("label", "sentence", "prediction")
    predicted
  }
  
  def main(args: Array[String]): Unit = {
    implicit val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("TfIdfExample")
      .getOrCreate()
    val evaled = evaluate("Картошка")
    val eval = evaled.rdd
    eval.foreach(f => {
      if(f.getDouble(0) != f.getDouble(2)) println(f.getDouble(0) + "--------" + f.getString(1))
    })
    evaled.show(300)
    val evalCount = eval.count
    val cit = eval.filter(_.getDouble(0) == 1.0)
    val citCount = cit.count
    val wrongCit = cit.filter(_.getDouble(2) == 0.0).count
    val correctCit = cit.filter(_.getDouble(2) == 1.0).count
    
    val maf = eval.filter(_.getDouble(0) == 0.0)
    val mafCount = maf.count
    val wrongMaf = maf.filter(_.getDouble(2) == 1.0).count
    val correctMaf = maf.filter(_.getDouble(2) == 0.0).count
    println("Количество игр мафией " + mafCount)
    println("Количество игр чижом " + citCount)
    println("Количество неправильно определенных мафов " + wrongMaf)
    println("Количество неправильно определенных чижей " + wrongCit)
    println("Количество правильных мафов " + correctMaf)
    println("Количество правильных чижей " + correctCit)
    println("Угадывание мафов " + correctMaf.toDouble / maf.count * 100)
    println("Угадывание чижей " + correctCit.toDouble / cit.count * 100)
    println("Угадывание процент " + (correctCit.toDouble + correctMaf) / evalCount * 100)
    spark.stop()
  }
}