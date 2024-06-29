package cn.izualzhy

import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.slf4j.LoggerFactory

class MockProcessingTimeTrigger private() extends Trigger[Any, TimeWindow] {
  val logger = LoggerFactory.getLogger(this.getClass)
  override def onElement(element: Any, timestamp: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    logger.info(s"onElement element:${element} timestamp:${timestamp} window:${window}")

    val stack = Thread.currentThread().getStackTrace.map(_.toString)
      .mkString("\n\t")
    logger.info(s"stack:\n${stack}")

    ctx.registerProcessingTimeTimer(window.maxTimestamp())
    TriggerResult.CONTINUE
  }

  override def onEventTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    logger.info(s"onEventTime time:${time} window:${window}")

    val stack = Thread.currentThread().getStackTrace.map(_.toString)
      .mkString("\n\t")
    logger.info(s"stack:\n${stack}")

    TriggerResult.CONTINUE
  }

  override def onProcessingTime(time: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    logger.info(s"onProcessingTime time:${time} window:${window}")

    val stack = Thread.currentThread().getStackTrace.map(_.toString)
      .mkString("\n\t")
    logger.info(s"stack:\n${stack}")

    TriggerResult.FIRE
  }

  override def clear(window: TimeWindow, ctx: Trigger.TriggerContext): Unit = {
    logger.info(s"clear window:${window}")

    val stack = Thread.currentThread().getStackTrace.map(_.toString)
      .mkString("\n\t")
    logger.info(s"stack:\n${stack}")

    ctx.deleteProcessingTimeTimer(window.maxTimestamp())
  }

  override def canMerge: Boolean = true

  override def onMerge(window: TimeWindow, ctx: Trigger.OnMergeContext): Unit = {
    logger.info(s"onMerge window:${window}")

    val stack = Thread.currentThread().getStackTrace.map(_.toString)
      .mkString("\n\t")
    logger.info(s"stack:\n${stack}")

    val windowMaxTimestamp = window.maxTimestamp()
    if (windowMaxTimestamp > ctx.getCurrentProcessingTime) {
      ctx.registerProcessingTimeTimer(windowMaxTimestamp)
    }
  }

  override def toString: String = "ProcessingTimeTrigger()"
}

object MockProcessingTimeTrigger {
  def create(): MockProcessingTimeTrigger = new MockProcessingTimeTrigger()
}
