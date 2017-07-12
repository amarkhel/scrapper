package com.supergloo.ml

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
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
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.feature.IndexToString
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.classification.NaiveBayes

object NaiveBayesApp {
  
  def evaluate(name:String)(implicit spark:SparkSession) = {
    val (training, test) = Util.tfidf2(name)

    val model = new NaiveBayes()
      .fit(training)

    // Select example rows to display.
    val predictions = model.transform(test)
    predictions.show()

    // Select (prediction, true label) and compute test error
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println("Test set accuracy = " + accuracy)
    val predicted = predictions.select("label", "sentence", "prediction")
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
    spark.stop
  }
}