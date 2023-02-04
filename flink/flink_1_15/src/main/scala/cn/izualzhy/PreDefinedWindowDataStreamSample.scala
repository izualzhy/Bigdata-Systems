package cn.izualzhy

import java.lang
import java.text.SimpleDateFormat
import java.time.Duration

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.{AggregateFunction, RichMapFunction}
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object PreDefinedWindowDataStreamSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)
  val userClicksCsvPath = Thread.currentThread().getContextClassLoader.getResource("user_clicks.csv").getPath

  env.readTextFile(userClicksCsvPath)
    .map(new SplitFunction)
    .assignTimestampsAndWatermarks(customWatermarkStrategy())
    .keyBy(_._1)
    .window(SlidingEventTimeWindows.of(Time.seconds(7), Time.seconds(2)))
    .aggregate(new CountAggregateFunction)
    .addSink(println(_))

  class SplitFunction extends RichMapFunction[String, (String, String, String)] {
    override def map(value: String): (String, String, String) = {
      val elems = value.split(",")
      (elems(0), elems(1), elems(2))
    }
  }

  def customWatermarkStrategy(): WatermarkStrategy[(String, String, String)] = {
    WatermarkStrategy.forBoundedOutOfOrderness[(String, String, String)](Duration.ofSeconds(2))
      .withTimestampAssigner(new SerializableTimestampAssigner[(String, String, String)] {
        override def extractTimestamp(element: (String, String, String), recordTimestamp: Long): Long = {
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(element._3).getTime
        }
      })
  }

  class CountProcessWindowFunction extends ProcessWindowFunction[(String, String, String), Long, String, TimeWindow] {
    override def process(key: String,
                         context: ProcessWindowFunction[(String, String, String), Long, String, TimeWindow]#Context,
                         elements: lang.Iterable[(String, String, String)],
                         out: Collector[Long]): Unit = {
//      context.window().getStart
    }
  }

  class CountAggregateFunction extends AggregateFunction[(String, String, String), Long, Long] {
    override def createAccumulator(): Long = 0L
    override def merge(a: Long, b: Long): Long = a + b
    override def add(value: (String, String, String), accumulator: Long): Long = {
      accumulator + 1
    }
    override def getResult(accumulator: Long): Long = {
      accumulator
    }
  }

//  val dataTypes = DataTypes.ROW(
//    DataTypes.FIELD("username", DataTypes.STRING()),
//    DataTypes.FIELD("click_url", DataTypes.STRING()),
//    DataTypes.FIELD("ts", DataTypes.TIMESTAMP(3))
//  )
//  TypeInformation.of(
//    new TypeHint[] {}
//  )
//  TypeConversions.fromDataTypeToLegacyInfo()
//  val typeInformation = TypeInformation.of(Ty)

//  val csvSchema = CsvSchema.builder()
//    .addColumn(new Column(0, "c1", CsvSchema.ColumnType.STRING))
//    .addColumn(new Column(0, "c1", CsvSchema.ColumnType.))
//  CsvReaderFormat.forSchema()

//  case class A(username: String, click_url: String)
//  val csvFormat = CsvReaderFormat.forPojo(classOf[A])
//    .
//  val csvFileSource = FileSource.forBulkFileFormat(
//    new StreamFormatAdapter(csvFormat),
//    new Path(userClicksCsvPath)
//  ).build()
//
//  env.fromSource(
//    csvFileSource,
//    WatermarkStrategy.noWatermarks(),
//    "user_clicks_source"
//  ).addSink(println(_))

  env.execute()

//  env.readTextFile("")

//  env.fromSource(
//    csvFileSource,
//    WatermarkStrategy.forBoundedOutOfOrderness[String, String, TimeStal](Duration.ofSeconds(2))
//      .withTimestampAssigner(new SerializableTimestampAssigner)
//  )
}
