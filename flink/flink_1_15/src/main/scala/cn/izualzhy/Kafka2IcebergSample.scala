package cn.izualzhy

import java.util.Properties

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.formats.common.TimestampFormat
import org.apache.flink.formats.json.JsonRowDataDeserializationSchema
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.DataTypes._
import org.apache.flink.table.api.{DataTypes, TableColumn, TableSchema}
import org.apache.flink.table.data.RowData
import org.apache.flink.table.runtime.typeutils.InternalTypeInfo
import org.apache.flink.table.types.logical.RowType

object Kafka2IcebergSample extends App {
  val paramTool = ParameterTool.fromArgs(args)
  val env = StreamExecutionEnvironment.getExecutionEnvironment;

  val checkpointConfig = env.getCheckpointConfig
  checkpointConfig.setCheckpointInterval(60000)
  checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

  val sourceStream = env.addSource(genKafkaSource(paramTool))

  def genKafkaSource(paramTool: ParameterTool): FlinkKafkaConsumer[RowData] = {
    val properties = new Properties()
    List("bootstrap.servers", "group.id").foreach(attr => properties.setProperty(attr, paramTool.get(attr)))

    val schema = TableSchema.builder()
      .add(TableColumn.physical("s", STRING()))
      .add(TableColumn.physical("kv", DataTypes.MAP(STRING(), STRING())))
      .add(TableColumn.physical("dt", STRING()))
      .add(TableColumn.physical("indt", STRING()))
      .add(TableColumn.physical("hour", STRING()))
      .build()
    val rowType = schema.toRowDataType.getLogicalType.asInstanceOf[RowType]
    val resultTypeInfo = InternalTypeInfo.of(rowType)

    val valueDeserializer = new JsonRowDataDeserializationSchema(
      rowType,
      resultTypeInfo,
      false,
      true,
      TimestampFormat.SQL)

    new FlinkKafkaConsumer(paramTool.get("topic"), valueDeserializer, properties)
  }
}
