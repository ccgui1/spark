package readmysql

import org.apache.spark.sql.SparkSession

object ReadFromMysql {
  def main(args: Array[String]): Unit = {
    val sc = SparkSession.builder().
      appName("ReadFromMysql").
      master("local[*]").
      getOrCreate()

    val options = Map("url" -> "jdbc:mysql://localhost:3306/test",
                      "driver" -> "com.mysql.jdbc.Driver",
                      "user"-> "root",
                      "password" -> "123456",
                      "dbtable"-> "user_info"
                      )
    val df6 = sc.read.format("jdbc").options(options).load()
    df6.show()
  }
}
