package cn.izualzhy

import org.apache.flink.api.common.state.ReducingStateDescriptor
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.slf4j.{Logger, LoggerFactory}

class EventTimeAndCountTrigger(maxCount: Long = 3) extends Trigger[Any, TimeWindow] {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val curCountDescriptor = new ReducingStateDescriptor[Long]("counter", (a, b) => a + b , classOf[Long])

  override def onElement(t: Any, l: Long, w: TimeWindow, triggerContext: Trigger.TriggerContext): TriggerResult = {
//    val stack = Thread.currentThread().getStackTrace.map(_.toString)
//      .mkString("\n\t")
//    logger.info(s"stack:\n${stack}")

    val curCount = triggerContext.getPartitionedState(curCountDescriptor)
    curCount.add(1L)
    val result = if (curCount.get() >= maxCount || w.maxTimestamp <= triggerContext.getCurrentWatermark) {
      curCount.clear()
      // 比如窗口时间周期内，提前因为达到 maxCount 触发
      // FIRE: maxCount 触发窗口内已经收到的数据参与计算；之后到达 maxTimestamp，这些数据仍然会计算一次
      // FIRE_AND_PURGE: maxCount 触发窗口内已经收到的数据参与计算；之后到达 maxTimestamp，这些数据不会再计算一次了
//      TriggerResult.FIRE
      TriggerResult.FIRE_AND_PURGE
    } else {
      triggerContext.registerEventTimeTimer(w.maxTimestamp)
      TriggerResult.CONTINUE
    }

    logger.info(s"onElement t:${t} l:${l} w:${w} ${Integer.toHexString(System.identityHashCode(w))} result:${result}")

    result
  }

  override def onProcessingTime(l: Long, w: TimeWindow, triggerContext: Trigger.TriggerContext): TriggerResult = {
    logger.info(s"onProcessingTime l:${l} w:${w} ${Integer.toHexString(System.identityHashCode(w))}")
    TriggerResult.CONTINUE
  }

  override def onEventTime(l: Long, w: TimeWindow, triggerContext: Trigger.TriggerContext): TriggerResult = {
//    val stack = Thread.currentThread().getStackTrace.map(_.toString)
//      .mkString("\n\t")
//    logger.info(s"stack:\n${stack}")

    val result = if (l == w.maxTimestamp) TriggerResult.FIRE
    else TriggerResult.CONTINUE
    logger.info(s"onEventTime l:${l} w:${w} ${Integer.toHexString(System.identityHashCode(w))} result:${result}")

    result
  }

  override def clear(w: TimeWindow, triggerContext: Trigger.TriggerContext): Unit = {
    logger.info(s"clear w:${w} ${Integer.toHexString(System.identityHashCode(w))}")

//    val stack = Thread.currentThread().getStackTrace.map(_.toString)
//      .mkString("\n\t")
//    logger.info(s"stack:\n${stack}")

    triggerContext.deleteEventTimeTimer(w.maxTimestamp)
    triggerContext.getPartitionedState(curCountDescriptor).clear()
  }
}
