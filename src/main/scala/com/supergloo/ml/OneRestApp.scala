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
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.classification.OneVsRest
import org.apache.spark.ml.linalg.SparseVector

// $example off$

object OneRestApp {

  def evaluate(name:String)(implicit spark:SparkSession) = {
    val sc = spark.sparkContext
    import spark.implicits._
    val (training, test) = Util.tfidf2(name)
    // instantiate the base classifier
    val classifier = new LogisticRegression()
      .setMaxIter(10)
      .setTol(1E-6)
      .setFitIntercept(true)

    // instantiate the One Vs Rest Classifier.
    val ovr = new OneVsRest().setClassifier(classifier)

    // train the multiclass model.
    val ovrModel = ovr.fit(training)

    // score the model on test data.
    val predictions = ovrModel.transform(test)

    // obtain evaluator.
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")

    // compute the classification error on test data.
    val accuracy = evaluator.evaluate(predictions)
    println(s"Test Error = ${1 - accuracy}")
    predictions.select("label", "sentence", "prediction")
  }
  
  def main(args: Array[String]): Unit = {
    implicit val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("TfIdfExample")
      .getOrCreate()
    evaluate("apaapaapa")
    spark.stop()
  }
}