package cn.izualzhy

import org.apache.flink.streaming.api.windowing.triggers.Trigger.TriggerContext
import org.apache.flink.streaming.api.windowing.triggers.{EventTimeTrigger, Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.slf4j.{Logger, LoggerFactory}

class MockEventTimeTrigger extends Trigger[Any, TimeWindow] {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def onElement(
                          element: Any,
                          timestamp: Long,
                          window: TimeWindow,
                          ctx: TriggerContext): TriggerResult = {
    logger.info(s"this:${this} onElement element:${element} timestamp:${timestamp} window:${window} ${Integer.toHexString(System.identityHashCode(window))}")
    if (window.maxTimestamp() <= ctx.getCurrentWatermark) {
      // if the watermark is already past the window fire immediately
      TriggerResult.FIRE
    } else {
      ctx.registerEventTimeTimer(window.maxTimestamp())
      TriggerResult.CONTINUE
    }
  }

  override def onEventTime(time: Long, window: TimeWindow, ctx: TriggerContext): TriggerResult = {
    logger.info(s"this:${this} onEventTime time:${time} window:${window} ${Integer.toHexString(System.identityHashCode(window))}")
    if (time == window.maxTimestamp) TriggerResult.FIRE else TriggerResult.CONTINUE
  }

  override def onProcessingTime(time: Long, window: TimeWindow, ctx: TriggerContext): TriggerResult = {
    logger.info(s"this:${this} onProcessingTime time:${time} window:${window} ${Integer.toHexString(System.identityHashCode(window))}")
    TriggerResult.CONTINUE
  }

  override def clear(window: TimeWindow, ctx: TriggerContext): Unit = {
    logger.info(s"this:${this} clear window:${window} ${Integer.toHexString(System.identityHashCode(window))}")
    ctx.deleteEventTimeTimer(window.maxTimestamp())
  }

  override def canMerge(): Boolean = true

  override def onMerge(window: TimeWindow, ctx: Trigger.OnMergeContext): Unit = {
    logger.info(s"this:${this} onMerge window:${window} ${Integer.toHexString(System.identityHashCode(window))}")
    val windowMaxTimestamp = window.maxTimestamp
    if (windowMaxTimestamp > ctx.getCurrentWatermark) {
      ctx.registerEventTimeTimer(windowMaxTimestamp)
    }
  }

  override def toString: String = "EventTimeTrigger()"
}

object MockEventTimeTrigger {
  def create(): MockEventTimeTrigger = new MockEventTimeTrigger()
}


