package readmysql


import org.apache.spark.sql.SparkSession



object ReadFromMysql {
  val spark1 = SparkSession.builder().
    appName("ReadFromMysql").
    master("local[*]").
    getOrCreate()
  def main(args: Array[String]): Unit = {

//连接本地mysql，并读取数据
    val options = Map("url" -> "jdbc:mysql://localhost:3306/test",
                      "driver" -> "com.mysql.jdbc.Driver",
                      "user"-> "root",
                      "password" -> "123456",
                      "dbtable"-> "user_info"
                      )
    val df6 = spark1.read.format("jdbc").options(options).load()
    df6.show()
    dataToMysql()

  }
  //往mysql里追加数据。
  def dataToMysql(): Unit={
    val df7_1 = spark1.createDataFrame(List(
      ("5", "Alice", 20),
      ("6", "Tom", 25),
      ("7", "Boris", 18))).toDF("id","name","age")
    val properties = new java.util.Properties()
    properties.setProperty("user","root")
    properties.setProperty("password","123456")

    import org.apache.spark.sql.SaveMode
    df7_1.write.mode(SaveMode.Append).jdbc("jdbc:mysql://localhost/test","user_info",properties)
  }
}
