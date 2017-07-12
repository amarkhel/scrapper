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
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.feature.IndexToString
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import java.io.File
import scala.util.Try
import scala.io.Source

object DesTreeApp {

  def evaluate(name: String)(implicit spark: SparkSession) = {
    val result = (10 to 100 by 1).par.map(run(_, name)).seq

    result.foreach(println)
    val best = result.sortBy(_._5).reverse
    
    best.foreach(r => println(s"count features = ${r._8}, accuracy maf = ${r._5}"))
    
    println("bessstt")
    best.take(10).foreach(println)
    best.take(1)(0)._9
    val best2 = result.filter(_._5 > 50).sortBy(_._1)
    
    
    println("bessstt2")
    best2.take(10).foreach(println)
    best2.take(1)(0)._9
  }

  def run(param: Int, name: String)(implicit spark: SparkSession) = {
    val (training, test) = Util.tfidf(name, param)

    val dt = new DecisionTreeClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")

    val pipeline = new Pipeline().setStages(Array(dt))

    val model = pipeline.fit(training)

    val predictions = model.transform(test)
    val predicted = predictions.select("label", "sentence", "prediction")

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    val rdd = predictions.select("label", "prediction").rdd
    val map = rdd.map(r => r.get(0).toString -> r.get(1).toString)
    val correct = map.map(d => if (d._1 == d._2) 1 else 0)
    val correctMaf = map.filter(_._1.toString == "0.0").map(d => if (d._1 == d._2) 1 else 0)
    val correctCit = map.filter(_._1.toString == "1.0").map(d => if (d._1 == d._2) 1 else 0)
    (1.0 - accuracy, correct.count, correct.sum.toDouble / correct.count * 100, correctMaf.count, correctMaf.sum.toDouble / correctMaf.count * 100, correctCit.count, correctCit.sum.toDouble / correctCit.count * 100, param, predicted)
  }

  def main(args: Array[String]): Unit = {
    /*implicit val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("TfIdfExample")
      .getOrCreate()
    evaluate("Bazinga")

    spark.stop()*/
/*    val dir = new File("C:\\Users\\amarkhel\\git\\youtrack-to-jira\\restdata")
  dir.listFiles.filter(_.isFile).toList.foreach { file =>
        if(file.getName.endsWith(".xml")) mv(file.getAbsolutePath, file.getAbsolutePath.replaceAll("", "")) else println()
    //println(file.getName)
    }

def mv(oldName: String, newName: String) = {
  val result = Try(new File(oldName).renameTo(new File(newName))).getOrElse(false)
  println(result)
}*/
  val filename = "C:\\Users\\amarkhel\\git\\youtrack-to-jira\\users.txt"
for (line <- Source.fromFile(filename).getLines().map(_.trim.split(" ")(1))) {
  println(s""" '$line' => '$line',""")
}
  }
}