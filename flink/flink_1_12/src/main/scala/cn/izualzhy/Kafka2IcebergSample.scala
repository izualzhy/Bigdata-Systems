package cn.izualzhy

import java.util
import java.util.Properties

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.formats.json.{JsonRowDataDeserializationSchema, TimestampFormat}
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.api.DataTypes.STRING
import org.apache.flink.table.api.{DataTypes, TableColumn, TableSchema}
import org.apache.flink.table.data.RowData
import org.apache.flink.table.runtime.typeutils.InternalTypeInfo
import org.apache.flink.table.types.logical.RowType
import org.apache.hadoop.conf.Configuration
import org.apache.iceberg.{PartitionSpec, Schema, Table}
import org.apache.iceberg.catalog.{Namespace, TableIdentifier}
import org.apache.iceberg.flink.{CatalogLoader, TableLoader}
import org.apache.iceberg.flink.sink.FlinkSink
import org.apache.iceberg.types.Types
import scala.collection.JavaConverters._

object Kafka2IcebergSample extends App {
  val paramTool = ParameterTool.fromArgs(args)
  val env = StreamExecutionEnvironment.getExecutionEnvironment;

  val checkpointConfig = env.getCheckpointConfig
  checkpointConfig.setCheckpointInterval(60000)
  checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

  val sourceStream = env.addSource(genKafkaSource(paramTool))
  icebergSink_hive(sourceStream, paramTool)

  println(env.getExecutionPlan)
  env.execute("test")

  def genKafkaSource(paramTool: ParameterTool): FlinkKafkaConsumer[RowData] = {
    val properties = new Properties()
    List("bootstrap.servers", "group.id").foreach(attr => properties.setProperty(attr, paramTool.get(attr)))

    val kafkaSchema = TableSchema.builder()
      .add(TableColumn.physical("s", STRING()))
      .add(TableColumn.physical("kv", DataTypes.MAP(STRING(), STRING())))
      .add(TableColumn.physical("dt", STRING()))
      .add(TableColumn.physical("indt", STRING()))
      .add(TableColumn.physical("hour", STRING()))
      .build()
    val rowType = kafkaSchema.toRowDataType.getLogicalType.asInstanceOf[RowType]
    val resultTypeInfo = InternalTypeInfo.of(rowType)

    val valueDeserializer = new JsonRowDataDeserializationSchema(
      rowType,
      resultTypeInfo,
      false,
      true,
      TimestampFormat.SQL)

    new FlinkKafkaConsumer(paramTool.get("topic"), valueDeserializer, properties)
  }

  private def icebergSink_hive(src: DataStream[RowData], tool: ParameterTool): Unit = {
    val properties = new util.HashMap[String, String]()
    properties.put("type", "iceberg")
    properties.put("catalog-type", "hive")
    properties.put("property-version", "1")
    properties.put("warehouse", tool.get("warehouse"))
    properties.put("uri", tool.get("uri"))
    val catalogLoader = CatalogLoader.hive("db2022", new Configuration(), properties)
    icebergSink(src, tool, catalogLoader)
  }

  val sinkSchema = new Schema(
    Types.NestedField.optional(1, "s", Types.StringType.get()),
    Types.NestedField.optional(2, "kv", Types.StringType.get()),
    Types.NestedField.optional(3, "dt", Types.StringType.get()),
    Types.NestedField.optional(4, "indt", Types.StringType.get()),
    Types.NestedField.optional(5, "hour", Types.StringType.get())
  )

  private def icebergSink(input: DataStream[RowData], tool: ParameterTool, loader: CatalogLoader): Unit = {
    println(
      s"""
        |namespace:${Namespace.of(tool.get("hive_db"))},
        |table:${Namespace.of(tool.get("hive_table"))}""".stripMargin)

    val catalog = loader.loadCatalog
    val identifier = TableIdentifier.of(Namespace.of(tool.get("hive_db")), tool.get("hive_table"))
    catalog.listTables(Namespace.of(tool.get("hive_db")))
      .asScala
      .foreach(tableIdentifier => println(tableIdentifier.toString))

    var table: Table = null
    if (catalog.tableExists(identifier)) {
      table = catalog.loadTable(identifier)
    } else {
      table = catalog.buildTable(identifier, sinkSchema)
        .withPartitionSpec(PartitionSpec.unpartitioned())
        .create
    }
    val tableLoader = TableLoader.fromCatalog(loader, identifier)
    FlinkSink.forRowData(input.javaStream)
      .table(table)
      .tableLoader(tableLoader)
      .writeParallelism(5)
      .build()
  }
}