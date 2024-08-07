package cn.izualzhy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeHint, TypeInformation}
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.slf4j.LoggerFactory

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import scala.collection.JavaConverters._
import scala.util._

object FlinkOnK8STest extends App {
  val logger = LoggerFactory.getLogger("FlinkOnK8sTest");
  val env = StreamExecutionEnvironment.getExecutionEnvironment;
  args.foreach(s => logger.info(s"arg : ${s}"))
  args.foreach(s => println(s"arg : ${s}"))

  val currentDate = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss"))
  val currentArg = args.iterator
  if (args(0) == "--properties-file") {
    currentArg.next()
    currentArg.next()
  }

  var bootstrapServers = currentArg.next()
  val checkpointInterval = currentArg.next().toInt
  val groupId = "tp-" + currentArg.next()
  val topic = currentArg.next()
  val sinkTopic = currentArg.next()

  if (args(0) != "--properties-file") {
    val fsStateBackend = new FsStateBackend(currentArg.next())
    env.setStateBackend(fsStateBackend);
  }
  val checkpointConfig = env.getCheckpointConfig
  checkpointConfig.setCheckpointInterval(checkpointInterval)
  checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
  checkpointConfig.setCheckpointTimeout(30000)
  checkpointConfig.setTolerableCheckpointFailureNumber(5)

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", bootstrapServers)
  properties.setProperty("group.id", groupId)
  properties.setProperty("auto.offset.reset", "earliest")

  val consumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties).setStartFromLatest()

  val sourceStream = env.addSource(consumer)
  sourceStream
    .map(i => Try {
      val elems = i.split(" ")
      (elems(0), elems(1).toInt)
    } match {
      case Success(value) => value
      case Failure(exception) => (exception.toString, -1)
    }).addSink(new BufferingSink)

  env.execute(this.getClass.getName)

  class BufferingSink() extends SinkFunction[(String, Int)] with CheckpointedFunction {
    val logger = LoggerFactory.getLogger("BufferingSink")
    @transient
    private var checkPointedState: ListState[(String, Int)] = _
    //    private val bufferedElements = ListBuffer[(String, Int)]()

    override def invoke(value: (String, Int), context: SinkFunction.Context): Unit = {
      //      if (value._2 > 0) {
      //        bufferedElements.append(value)
      //      }
      println(s"receive value:${value}")
      logger.info(s"receive value:${value}")

      if (value._2 == 123456789) {
        throw new RuntimeException("get a number. quit.")
      }
    }

    override def snapshotState(context: FunctionSnapshotContext): Unit = {
      //      checkPointedState.clear()
      //      for (element <- bufferedElements) {
      //        checkPointedState.add(element)
      //      }
    }

    override def initializeState(context: FunctionInitializationContext): Unit = {
      val descriptor = new ListStateDescriptor[(String, Int)](
        "buffered-elements",
        TypeInformation.of(new TypeHint[(String, Int)]() {})
      )

      checkPointedState = context.getOperatorStateStore.getListState(descriptor);
      if (context.isRestored) {
        println(s"initializeState checkPointedStated.size:${checkPointedState.get().asScala.size}")
        checkPointedState.get().asScala.foreach(i => {
          println(s"state: (${i._1.length}, ${i._2})")
        })
      }
    }
  }

}
