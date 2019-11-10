package com.ccgui.sparkstream

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

object QueueRdd {
   def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[2]").setAppName("QueueRdd")
    val ssc = new StreamingContext(conf, Seconds(1))
    // Create the queue through which RDDs can be pushed to
    // a QueueInputDStream
    //创建 RDD 队列
    val rddQueue = new mutable.SynchronizedQueue[RDD[Int]]()
    // Create the QueueInputDStream and use it do some processing
    // 创建 QueueInputDStream
    val inputStream = ssc.queueStream(rddQueue)
    //处理队列中的 RDD 数据
    val mappedStream = inputStream.map(x => (x % 10, 1))
    val reducedStream = mappedStream.reduceByKey(_ + _)
    //打印结果
    reducedStream.print()

    //启动计算
    ssc.start()
    // Create and push some RDDs into
    for (i <- 1 to 30) {
      rddQueue += ssc.sparkContext.makeRDD(1 to 300, 10)
      Thread.sleep(2000)
      //通过程序停止 StreamingContext 的运行
      //ssc.stop()
    }
  }

}
