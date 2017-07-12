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

object PerceptronApp {
  
  def evaluate(name:String)(implicit spark:SparkSession) = {
    val sc = spark.sparkContext
    import spark.implicits._
    // specify layers for the neural network:
    // input layer of size 4 (features), two intermediate of size 5 and 4
    // and output of size 3 (classes)

    val layers = Array[Int](50, 100, 100, 2)
    val (training, test) = Util.tfidf2(name)
    // create the trainer and set its parameters
    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setMaxIter(100)

    // train the model
    val model = trainer.fit(training)

    // compute accuracy on the test set
    val result = model.transform(test)
    val predictionAndLabels = result.select("label", "sentence", "prediction")
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")

    println("Test set accuracy = " + evaluator.evaluate(predictionAndLabels))
    predictionAndLabels
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