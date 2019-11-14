package averageudaf

import org.apache.spark.sql.SparkSession

object UseUDAF {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().
      appName("UseUDAF").
      master("local[*]").
      getOrCreate()

    val option = Map("url" -> "jdbc:mysql://localhost:3306/test",
      "driver" -> "com.mysql.jdbc.Driver",
      "user" -> "root",
      "password" -> "123456",
      "dbtable"-> "user_info"
      )
    val df = ss.read.format("jdbc").options(option).load()
    df.createTempView("user_info1")

    ss.udf.register("toDouble",(column : Any) => column.toString.toDouble)
    ss.udf.register("avgUDAF",  averageudaf.AverageUDAF)
    ss.sql("select name,avgUDAF(toDouble(age)) as avgAge from user_info1 group by name").show()
  }
}
