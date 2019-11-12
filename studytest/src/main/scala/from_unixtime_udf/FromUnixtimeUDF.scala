package from_unixtime_udf

import java.util
//example for using function "from_unixtime"(this function can
// format the timestamp as string to output)
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._

object FromUnixtimeUDF {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().
      appName("FromUnixtimeUDF").
      master("local[*]").getOrCreate()

    val schema = StructType(List(
      StructField("name", StringType, nullable = false),
      StructField("age", IntegerType, nullable = false),
      StructField("create_time", LongType, nullable = false)
    ))

    val javaList = new util.ArrayList[Row]()
    javaList.add(Row("Alice", 20, System.currentTimeMillis() / 1000))
    javaList.add(Row("Tom", 18, System.currentTimeMillis() / 1000))
    javaList.add(Row("Bories", 30, System.currentTimeMillis() / 1000))

    val df1 = ss.createDataFrame(javaList,schema)
    df1.show()
    df1.createTempView("t_user")
    ss.sql("select name,age,from_unixtime(create_time, 'yyyy-MM-dd HH:mm:ss') from t_user").show

  }
}
