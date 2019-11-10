package testmap01

import org.apache.spark.{SparkConf, SparkContext}

object Maptest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[1]").setAppName("maptest")
    val sc = new SparkContext(conf)
    val mapdata = Array(("1","B"),
      ("2","B"),
      ("3","C"),
      ("1","D"))
    val mdBroadcast = sc.broadcast(mapdata)
    val mapRDD = sc.parallelize(mapdata,2)
    val resultRDD = mapRDD.aggregateByKey(collection.mutable.Set[String]())(
      (ziset,zm) => ziset += zm,
      (ziset1,ziset2) => ziset1 ++= ziset2
    )
    resultRDD.collect().foreach(println)
    mdBroadcast.unpersist()
  }
}
