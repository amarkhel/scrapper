/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package com.amarkhel

import org.apache.spark.{SparkConf, SparkContext}

import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object CorrelationsExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("BinarizerExample")
      .getOrCreate().sparkContext
    

    // $example on$
    val seriesX: RDD[Double] = spark.parallelize(Array(1, 2, 3, 3, 5))  // a series
    // must have the same number of partitions and cardinality as seriesX
    val seriesY: RDD[Double] = spark.parallelize(Array(11, 20, 34, 33, 55))

    // compute the correlation using Pearson's method. Enter "spearman" for Spearman's method. If a
    // method is not specified, Pearson's method will be used by default.
    val correlation: Double = Statistics.corr(seriesX, seriesY, "spearman")
    println(s"Correlation is: $correlation")

    val data: RDD[Vector] = spark.parallelize(
      Seq(
        Vectors.dense(1.0, 10.0, 100.0, 1.0),
        Vectors.dense(2.0, 22.0, 178.0, 1.2),
        Vectors.dense(10.0, 104.0, 1023.0, 1.3),
        Vectors.dense(5.0, 45.0, 523.0, 4.0),
        Vectors.dense(15.0, 159.0, 1534.0, 17.0))
    )  // note that each Vector is a row and not a column

    val correlMatrix: Matrix = Statistics.corr(data, "pearson")
    
    println(correlMatrix.toString)
    
    val correlMatrix2: Matrix = Statistics.corr(data, "spearman")
    
    println(correlMatrix2.toString)

    spark.stop()
  }
}

