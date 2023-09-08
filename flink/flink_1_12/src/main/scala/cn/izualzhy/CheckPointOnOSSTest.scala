package cn.izualzhy

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeHint, TypeInformation}
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import java.util.Properties
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

object CheckPointOnOSSTest extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment;

  val checkpointConfig = env.getCheckpointConfig
  checkpointConfig.setCheckpointInterval(args(3).toInt)
  checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
  checkpointConfig.setCheckpointTimeout(60000)
  checkpointConfig.setTolerableCheckpointFailureNumber(10)

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", args(2))
  properties.setProperty("group.id", "test_ng2")

  args.foreach(i => println(s"args:${i}"))

  val consumer = new FlinkKafkaConsumer[String]("TestSQLSource", new SimpleStringSchema(), properties)
  consumer.setStartFromEarliest();

  val sourceStream = env.addSource(consumer)
  sourceStream
    .map(i => (i, i.toInt))
    .addSink(new BufferingSink)

  env.execute(this.getClass.getName)

  class BufferingSink() extends SinkFunction[(String, Int)] with CheckpointedFunction {
    @transient
    private var checkPointedState: ListState[(String, Int)] = _

    private var elementSize: Int = 0
    private val constantString: String = "a" * 1024 * 1024

    private val bufferedElements = ListBuffer[(String, Int)]()

    override def invoke(value: (String, Int), context: SinkFunction.Context): Unit = {
      elementSize = value._2
      println(s"receive value:${}", value)

      val stackTrace = Thread.currentThread().getStackTrace.map(i => i.toString).mkString("\t\n")
      println(s"stackTrace:\n${stackTrace}")
    }

    override def snapshotState(context: FunctionSnapshotContext): Unit = {
      checkPointedState.clear()
      //      for (element <- bufferedElements) {
      //        checkPointedState.add(element)
      //      }

      val maxCkElementSize = if (elementSize > 1024) 1024 else elementSize
      (0 to maxCkElementSize).foreach(i => {
        checkPointedState.add((constantString, 1))
      })

      if (elementSize > 0 && elementSize <= 10001 && (elementSize%2 == 0)) {
        if (scala.util.Random.nextInt(elementSize) == 0) {
          Thread.sleep(360000)
        }
      }
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
          println(s"state: (string'size:${i._1.length} int:${i._2})")
        })
        //        for (element <- checkPointedState.get()) {
        //          bufferedElements += element
        //        }
      }

    }
  }
}
