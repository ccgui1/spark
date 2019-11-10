package com.ccgui.ch5kmeans

import org.apache.spark.SparkConf
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{OneHotEncoder, StandardScaler, StringIndexer, VectorAssembler}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}

import scala.util.Random

object RunKMeans {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Ch5KMeans").setMaster("local[*]")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    spark.sparkContext.setCheckpointDir("D:\\tmp")
    val data = spark.read.
      option("inferSchema",true).
      option("header",false).
      csv("D:\\360安全浏览器下载\\ch5\\kddcup.data.corrected").
      toDF(
        "duration", "protocol_type", "service", "flag",
        "src_bytes", "dst_bytes", "land", "wrong_fragment", "urgent",
        "hot", "num_failed_logins", "logged_in", "num_compromised",
        "root_shell", "su_attempted", "num_root", "num_file_creations",
        "num_shells", "num_access_files", "num_outbound_cmds",
        "is_host_login", "is_guest_login", "count", "srv_count",
        "serror_rate", "srv_serror_rate", "rerror_rate", "srv_rerror_rate",
        "same_srv_rate", "diff_srv_rate", "srv_diff_host_rate",
        "dst_host_count", "dst_host_srv_count",
        "dst_host_same_srv_rate", "dst_host_diff_srv_rate",
        "dst_host_same_src_port_rate", "dst_host_srv_diff_host_rate",
        "dst_host_serror_rate", "dst_host_srv_serror_rate",
        "dst_host_rerror_rate", "dst_host_srv_rerror_rate",
        "label"
      )

    data.cache()

    val runKMeans = new RunKMeans(spark)

    runKMeans.clusteringTake0(data)

    data.unpersist()

  }
}

class RunKMeans(private val spark: SparkSession){


  import spark.implicits._

  def clusteringTake0(data: DataFrame): Unit = {
    data.select("label").groupBy("label").count().orderBy($"count".desc).show(20)

    val numericOnly = data.drop("protocol_type","service","flag").cache()

    val assembler = new VectorAssembler().
      setInputCols(numericOnly.columns.filter(_!="label")).
      setOutputCol("featureVector")

    val kmeans = new KMeans().
        setSeed(Random.nextLong()).
        setPredictionCol("cluster").
        setFeaturesCol("featureVector")

    val pipeline = new Pipeline().setStages(Array(assembler,kmeans))
    val pipelineModel = pipeline.fit(numericOnly)
    val kmeansModel = pipelineModel.stages.last.asInstanceOf[KMeansModel]

    kmeansModel.clusterCenters.foreach(println)

    val withCluster = pipelineModel.transform(numericOnly)

    withCluster.select("cluster","label").
      groupBy("cluster", "label").count().
      orderBy($"cluster",$"count".desc).
      show(25)

    numericOnly.unpersist()
  }

  // Clustering,Take 1
  def clusteringScore0(data: DataFrame, k: Int): Double = {
    val assembler = new VectorAssembler().
      setInputCols(data.columns.filter(_!="label")).
      setOutputCol("featureVector")

    val kmeans = new KMeans().
      setSeed(Random.nextLong()).
      setK(k).
      setPredictionCol("cluster").
      setFeaturesCol("featureVector")

    val pipeline = new Pipeline().setStages(Array(assembler, kmeans))

    val kMeansModel = pipeline.fit(data).stages.last.asInstanceOf[KMeansModel]
    kMeansModel.computeCost(assembler.transform(data)) / data.count()
  }

  def clusteringScore1(data: DataFrame,k: Int): Double = {
    val assembler = new VectorAssembler().
      setInputCols(data.columns.filter(_!="label")).
      setOutputCol("featureVector")

    val kmeans = new KMeans().
      setSeed(Random.nextLong()).
      setK(k).
      setPredictionCol("cluster").
      setFeaturesCol("featureVector").
      setMaxIter(40).
      setTol(1.0e-5)

    val kmeansModel = new Pipeline().fit(data).stages.last.asInstanceOf[KMeansModel]
    kmeansModel.computeCost(assembler.transform(data)) / data.count()
  }

  def clusteringTake1(data: DataFrame): Unit = {
    val numericOnly = data.drop("protocol_type","service","flag").cache()
    (20 to 100 by 20).map(k => (k, clusteringScore0(numericOnly, k))).foreach(println)
    (20 to 100 by 20).map(k => (k, clusteringScore1(numericOnly, k))).foreach(println)

    numericOnly.cache()
  }

  // Clustering, Take 2
  def clusteringScore2(data: DataFrame, k: Int): Double = {
    val assembler = new VectorAssembler().
      setInputCols(data.columns.filter(_!="label")).
      setOutputCol("featureVector")

    val scaler = new StandardScaler()
        .setInputCol("featureVector")
        .setOutputCol("scaledFeatureVector")
        .setWithStd(true)
        .setWithMean(false)

    val kmeans = new KMeans().
      setSeed(Random.nextLong()).
      setK(k).
      setPredictionCol("cluster").
      setFeaturesCol("scaledFeatureVector").
      setMaxIter(40).
      setTol(1.0e-5)

    val pipeline = new Pipeline().setStages(Array(assembler, scaler, kmeans))
    val pipelineModel = pipeline.fit(data)

    val kmeansModel = pipelineModel.stages.last.asInstanceOf[KMeansModel]
    kmeansModel.computeCost(pipelineModel.transform(data)) / data.count()
  }
    def clusteringTake2(data: DataFrame): Unit = {
      val numericOnly = data.drop("protocol_type","service","flag").cache()
      (60 to 270 by 30).map(k => (k, clusteringScore2(numericOnly, k))).foreach(println)
      numericOnly.unpersist()
    }

  // Clustering, Take 3
  def oneHotPipline(inputCol:String): (Pipeline,String) = {
    val indexer = new StringIndexer().
      setInputCol(inputCol).
      setOutputCol(inputCol + "_indexed")
    val encoder = new OneHotEncoder().
      setInputCol(inputCol + "_indexed").
      setOutputCol(inputCol + "_vec")
    val pipeline = new Pipeline().setStages(Array(indexer, encoder))
    (pipeline, inputCol + "_vec")
  }

  def cluster
}