package averageudaf

import org.apache.spark.sql.{Encoder, Encoders, Row}
import org.apache.spark.sql.expressions.Aggregator
case class Buffer(var age:Double,var count: Long)

object AverageFemaleUDAF extends Aggregator[Row, Buffer,Double]{
  override def zero : Buffer = Buffer(0.0, 0L)

  override def reduce(b: Buffer, a: Row) : Buffer= {
    if (a.getString(2) == "Female"){
      b.age += a.getInt(1)
      b.count += 1
    }
    b
  }

  override def merge(b1: Buffer, b2: Buffer) : Buffer= {
    b1.age += b2.age
    b1.count += b2.count
    b1
  }

  override def finish(reduction: Buffer) : Double= reduction.age /reduction.count

  override def bufferEncoder : Encoder[Buffer] = Encoders.product

  override def outputEncoder :Encoder[Double] = Encoders.scalaDouble
}
