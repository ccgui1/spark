package accumulatorv2


import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}


object AccTest {
  case class User(name: String, sex: String, age: Int)
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("AccumulatorV2").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val userArray = Array(User("Alice", "Female", 11),
      User("Bob", "Male", 16),
      User("Thomas", "Female", 20),
      User("Boris", "Female", 12))
    val userRDD = sc.parallelize(userArray, 2)
    lazy  val userAccumulator = new UserAccumulator[User]
    sc.register(userAccumulator, "用户自定义累加器")
    userRDD.foreach(userAccumulator.add)
    println(userAccumulator)

  }
  class UserAccumulator[T] extends AccumulatorV2[T, Array[Int]]{

    //Array(男性用户数，女性用户数，11岁及以下年纪用户数，12～17岁用户数)
    private val _array: Array[Int] = Array(0, 0, 0, 0)
    override def isZero: Boolean = { _array.mkString("").toLong == 0L}

    override def copy(): AccumulatorV2[T, Array[Int]] = {
      val newAcc = new UserAccumulator[T]
      _array.copyToArray(newAcc._array)
      newAcc
    }

    override def reset(): Unit = {
      for (i <- _array.indices){
        _array(i) = 0
      }
    }

    override def add(v: T): Unit = {
      val user = v.asInstanceOf[User]
      if (user.sex == "Female") {
        _array(0) += 1
      }else{
        _array(1) += 1
      }


      if (user.age < 12){
        _array(2) += 1
      }else if (user.age < 18){
        _array(3) += 1
      }
    }

    override def merge(other: AccumulatorV2[T, Array[Int]]): Unit = {
      val o = other.asInstanceOf[UserAccumulator[T]]
      _array(0) += o._array(0)
      _array(1) += o._array(1)
      _array(2) += o._array(2)
      _array(3) += o._array(3)
    }

    override def value: Array[Int] = {
      _array
    }

    override def toString() : String = {
      getClass.getSimpleName + s"(id: $id, name: $name, value: ${value.mkString(",")})"
    }
  }
}

