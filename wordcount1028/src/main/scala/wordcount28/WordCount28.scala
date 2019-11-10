package wordcount28

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object WordCount28 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("wordcount").setMaster("local[*]")
    System.setProperty("master", "hdfs")
    System.setProperty("HADOOP_USER_NAME","root")
 //   val sc =  SparkSession.builder().config(conf).getOrCreate()
      val sc = new SparkContext(conf)
   val out = sc.textFile("hdfs://master:9820/input/*").
      flatMap(_.split(" ")).
      map((_,1)).
      reduceByKey(_+_,1).
      sortBy(_._2,false)
   //   saveAsTextFile("hdfs://master:9820/output2")
    println(out.collect.mkString("\n"))
    out.saveAsTextFile("hdfs://master:9820/output3")
    sc.stop()
  }
}
