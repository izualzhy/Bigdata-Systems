package cn.izualzhy

import org.apache.flink.api.common.state.CheckpointListener
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala._
import org.slf4j.LoggerFactory

/**
 * Author: yingshin
 * Date: 2022/1/4 20:41
 * Package:
 * Description:
 *
 */
object CheckpointThreadDemo extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.enableCheckpointing(60000)
  env.setParallelism(1)

  class TestSourceFunction extends SourceFunction[Int] with CheckpointedFunction with CheckpointListener {
    var running = true
    private val logger = LoggerFactory.getLogger(this.getClass)

    override def run(ctx: SourceFunction.SourceContext[Int]): Unit = {
      var i = 1
      while (running) {
        logger.info(s"i:${i}")
        logger.info(s"stack trace:\n${Thread.currentThread().getStackTrace.mkString("\n")}")
        ctx.collect(i)
        Thread.sleep(30000)
        i += 1
      }
    }

    override def cancel(): Unit = {
      running = false
    }

    override def initializeState(context: FunctionInitializationContext): Unit = { }

    override def snapshotState(context: FunctionSnapshotContext): Unit = {
      logger.info(s"checkpointId:${context.getCheckpointId}")
      logger.info(s"stack trace:\n${Thread.currentThread().getStackTrace.mkString("\n")}")
      Thread.sleep(3000)
    }

    override def notifyCheckpointComplete(checkpointId: Long): Unit = {
      logger.info(s"checkpointId:$checkpointId")
//      logger.info(s"stack trace:\n${Thread.currentThread().getStackTrace.mkString("\n")}")
    }
  }

  class TestSinkFunction extends SinkFunction[Int] with CheckpointedFunction with CheckpointListener {
    private val logger = LoggerFactory.getLogger(this.getClass)

    override def invoke(value: Int, context: SinkFunction.Context): Unit = {
      logger.info(s"value:${value}")
      logger.info(s"stack trace:\n${Thread.currentThread().getStackTrace.mkString("\n")}")
    }

    override def initializeState(context: FunctionInitializationContext): Unit = { }

    override def snapshotState(context: FunctionSnapshotContext): Unit = {
      logger.info(s"checkpointId:${context.getCheckpointId}")
      logger.info(s"stack trace:\n${Thread.currentThread().getStackTrace.mkString("\n")}")
      Thread.sleep(3000)
    }

    override def notifyCheckpointComplete(checkpointId: Long): Unit = {
      logger.info(s"checkpointId:$checkpointId")
//      logger.info(s"stack trace:\n${Thread.currentThread().getStackTrace.mkString("\n")}")
    }
  }

  env.addSource(new TestSourceFunction).name("test-source")
    .keyBy(i => i)
    .addSink(new TestSinkFunction).name("test-sink")

  println(env.getExecutionPlan)
  env.execute(this.getClass.getCanonicalName)

}
