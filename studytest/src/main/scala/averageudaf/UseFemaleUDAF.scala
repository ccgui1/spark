package averageudaf


import java.util


import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._

object UseFemaleUDAF {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().appName("UseFemaleUDAF").master("local[*]").getOrCreate()

    val schema = StructType(
      StructField("name", StringType, nullable = false) ::
      StructField("age", IntegerType, nullable = false) ::
      StructField("sex", StringType, nullable = false) :: Nil
    )
    val javaList = new util.ArrayList[Row]()
    javaList.add(Row("Alice", 20, "Female"))
    javaList.add(Row("Toms", 18, "Male"))
    javaList.add(Row("Boris", 30, "Male"))
    val df1 = ss.createDataFrame(javaList,schema)
    df1.show()


    val femaleAvgAge = averageudaf.AverageFemaleUDAF.toColumn.name("female_average_age")
    val result = df1.select(femaleAvgAge)
    result.show
  }
}


