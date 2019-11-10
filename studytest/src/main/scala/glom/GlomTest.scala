package glom

import org.apache.spark.{SparkConf, SparkContext}

object GlomTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("glomTest").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rddData1 = sc.parallelize(1 to 10,5)
    println(rddData1.collect().mkString(" "))
    val rddData2 = rddData1.glom
    //rddData2.take(100).foreach(println)
    rddData2.saveAsTextFile("D:\\1.txt")

  }
}
