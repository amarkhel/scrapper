package com.supergloo.ml

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.feature.IDF
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.SparseVector
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.Pipeline

object Util {

  val cache = new collection.mutable.HashMap[String, DataFrame]
  def tfidf(name: String, countFeatures: Int = 200)(implicit spark: SparkSession) = {
    val labeled = hash(namedData(name), countFeatures)
    val citiz = labeled.filter("label = 1.0")
    val mafia = labeled.filter("label = 0.0")
    val Array(citTrain, citTest) = citiz.randomSplit(Array(0.3, 0.7), seed = 11L)
    val Array(mafTrain, mafTest) = mafia.randomSplit(Array(0.7, 0.3), seed = 11L)
    val training = citTrain.union(mafTrain)
    val test = citTest.union(mafTest)
    training.cache()
    test.cache()
    (training, test)
  }

  def tfidf2(name: String)(implicit spark: SparkSession) = {
    val labeled = hash(overallData)
    val citiz = labeled.filter("label = 1.0")
    val mafia = labeled.filter("label = 0.0")
    val Array(citTrain, citTest) = citiz.randomSplit(Array(0.27, 0.73), seed = 11L)
    val Array(mafTrain, mafTest) = mafia.randomSplit(Array(0.8, 0.2), seed = 11L)
    val training = citTrain.union(mafTrain)
    val testing = hash(namedData(name))
    training.cache()
    testing.cache()
    (training, testing)
  }

  def loadSentences(file: String)(implicit spark: SparkSession) = {
    if (cache.get(file).isDefined) cache.get(file).get else {
      val sc = spark.sparkContext
      val documents = sc.textFile(file, 20).map(f => {
        val spl = f.split(",")
        val label = spl(0).toDouble
        val content = if (spl.length > 1) spl(1) else ""
        (label, content)
      }).filter(!_._2.isEmpty)
      val sentenceData = spark.createDataFrame(documents).toDF("label", "sentence")
      sentenceData.cache()
      cache.put(file, sentenceData)
      sentenceData
    }

  }

  def overallData(implicit spark: SparkSession) = loadSentences("stat2016full/overall.txt")

  def namedData(name: String)(implicit spark: SparkSession) = loadSentences("stat2016full/" + name + ".txt")

  def hash(d: DataFrame, countFeatures: Int = 200)(implicit spark: SparkSession) = {
    /*import spark.implicits._
    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val wordsData = tokenizer.transform(d)
    val hashingTF = new HashingTF()
      .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(50)
    val featurizedData = hashingTF.transform(wordsData)
    // alternatively, CountVectorizer can also be used to get term frequency vectors

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)
    val b = rescaledData.select("label", "features", "sentence")
    val labeled = b.map(row => {
      val lab = row.getDouble(0)
      val features = row.getAs[org.apache.spark.ml.linalg.SparseVector]("features")
      val sentence = row.getString(2)
      (lab, features, sentence)
    }).toDF("label", "features", "sentence")
    labeled*/
    val tokenizer = new Tokenizer()
      .setInputCol("sentence")
      .setOutputCol("words")
    val hashingTF = new HashingTF()
      .setNumFeatures(countFeatures)
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("rawFeatures")
    val idf = new IDF().setInputCol(hashingTF.getOutputCol).setOutputCol("features")
    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, hashingTF, idf))

    // Fit the pipeline to training documents.
    val model = pipeline.fit(d)
    model.transform(d).select("label", "features", "sentence")
  }
}