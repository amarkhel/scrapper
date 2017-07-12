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
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.classification.RandomForestClassificationModel
import org.apache.spark.ml.classification.GBTClassifier
import org.apache.spark.ml.classification.GBTClassificationModel

object GBTApp {

  def evaluate(name:String)(implicit spark:SparkSession) = {
    val (training, test) = Util.tfidf2(name)
    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(training)
    // Automatically identify categorical features, and index them.
    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4) // features with > 4 distinct values are treated as continuous.
      .fit(training)

    // Train a DecisionTree model.
    val dt = new GBTClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")
      .setMaxIter(10)

    // Convert indexed labels back to original labels.
    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    // Chain indexers and tree in a Pipeline.
    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, dt, labelConverter))

    // Train model. This also runs the indexers.
    val model = pipeline.fit(training)

    // Make predictions.
    val predictions = model.transform(test)

    // Select example rows to display.
    val predicted = predictions.select("label", "sentence", "predictedLabel")

    // Select (prediction, true label) and compute test error.
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println("Test Error = " + (1.0 - accuracy))

    val treeModel = model.stages(2).asInstanceOf[GBTClassificationModel]
    println("Learned classification tree model:\n" + treeModel.toDebugString)

    val rdd = predictions.select("label", "predictedLabel").rdd
    val map = rdd.map(r => r.get(0).toString -> r.get(1))
    val correct = map.map(d => {
      if (d._1 == d._2) 1 else 0
    })
    println("prediction accuracy = " + correct.sum.toDouble / correct.count * 100)
    predicted
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