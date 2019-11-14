package averageudaf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._

object AverageUDAF extends UserDefinedAggregateFunction{
  override def inputSchema: StructType = {
    StructType(
      StructField("numInput", DoubleType, nullable = true) :: Nil
    )
  }

  override def bufferSchema: StructType = {
    StructType(
      StructField("buffer1", DoubleType, nullable = true) ::
      StructField("buffer2", LongType, nullable = true) :: Nil
    )
  }

  override def dataType: DataType = DoubleType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer.update(0, 0.0)
    buffer.update(1, 0L)
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    buffer.update(0, buffer.getDouble(0) + input.getDouble(0))
    buffer.update(1, buffer.getLong(1) + 1)
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    buffer1.update(0, buffer1.getDouble(0) + buffer2.getDouble(0))
    buffer1.update(1, buffer1.getLong(1) +  buffer2.getLong(1))
  }

  override def evaluate(buffer: Row): Any = {
    buffer.getDouble(0) / buffer.getLong(1)
  }
}
