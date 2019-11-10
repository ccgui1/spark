package sparksqltest01

import org.apache.spark.sql.SparkSession

object SqlTest01 {
  case class User(name:String, age:BigInt, sex:String, addr:Array[String])
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().
      master("local[*]").
      appName("SqlTest").
      getOrCreate()

    import spark.implicits._

    val df1 = spark.read.json("hdfs://master:9000/user.json")
    df1.show()

    val ds1 = spark.read.json("hdfs://master:9000/user.json").as[User]
    ds1.show()
    ds1.filter(_.sex== "Female").filter(_.age < 25).show()
    spark.stop()

  }
}
