package spark_udtf


import java.util

import org.apache.spark.sql.{Encoder, Row, SparkSession}
import org.apache.spark.sql.types._

object SparkUDTF {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().appName("sparkUDTF").master("local[*]").getOrCreate()

    val schema = StructType(
      StructField("movie", StringType, nullable = false) ::
      StructField("category", StringType, nullable = false) :: Nil
    )

    val javaList = new util.ArrayList[Row]()
    javaList.add(Row("《疑犯追踪》","战争，动作，科幻"))
    javaList.add(Row("《叶问》", "动作，战争"))

    val df1 = ss.createDataFrame(javaList,schema)
//    df1.show()
    implicit val flatMapEncoder: Encoder[(String, String)] = org.apache.spark.sql.Encoders.kryo[(String, String)]

    val tableArray = df1.flatMap(row => {
        val listTuple = new scala.collection.mutable.ListBuffer[(String,String)]()
        val categoryArray = row.getString(1).split("，")
        for( c <- categoryArray){
          listTuple.append((row.getString(0), c))
        }
      listTuple
      }).collect()
    val df2 = ss.createDataFrame(tableArray).toDF("movie", "category")
    df2.show
  }
}
