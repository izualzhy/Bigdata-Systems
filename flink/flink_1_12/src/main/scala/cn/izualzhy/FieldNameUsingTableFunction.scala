package cn.izualzhy

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.table.api.bridge.scala.{StreamTableEnvironment, tableConversions}
import org.apache.flink.table.functions.TableFunction
import org.apache.flink.types.Row

object FieldNameUsingTableFunction extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)
  env.enableCheckpointing(60000)
  val tEnv = StreamTableEnvironment.create(env)

  val source = env.fromElements(
    "1 a hello",
    "2 b world",
  )
  val source_table = tEnv.fromDataStream(source).as("a")

//  tEnv.createTemporaryFunction("test_table_udf", new TestTableFunction)
  tEnv.registerFunction("test_table_udf", new TestTableFunction)
  tEnv.createTemporaryView("source_table", source_table)

  val data_table = tEnv.sqlQuery("SELECT b, c, d FROM source_table, LATERAL TABLE(test_table_udf(a)) AS T(b, c, d)")
  data_table.printSchema()
  data_table.toAppendStream[Row].print()

  tEnv.createTemporaryView("data_table", data_table)
  tEnv.from("data_table").printSchema()
  tEnv.executeSql("SELECT * FROM data_table").print()

  env.execute("")

  class TestTableFunction extends TableFunction[(Int, String, String)] {
    def eval(str: String): Unit = {
      val res = str.split(" ")
      assert(res.length == 3)
      collect(res(0).toInt, res(1), res(2))
    }
  }
}