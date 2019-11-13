package touppercaseUDF

import org.apache.spark.sql.SparkSession
import from_unixtime_udf.FromUnixtimeUDF
object ToUpperCaseUDF {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().
      appName("ToUpperCaseUDF").
      master("local[*]").
      getOrCreate()

    ss.udf.register("toUpperCaseUDF",(column: String) => column.toUpperCase)
    ss.sql("select name,age,from_unixtime(create_time, 'yyyy-MM-dd HH:mm:ss') " +
      "from t_user").show
  }
}
