package cn.izualzhy

import spray.json.DefaultJsonProtocol._
import spray.json._

case class Checkpoint(counts: Counts, summary: Summary, latest: Latest, history: Seq[Completed])

case class Completed(
                      `@class`: String,
                      id: Int,
                      status: String,
                      is_savepoint: Boolean,
                      trigger_timestamp: Long,
                      latest_ack_timestamp: Long,
                      state_size: Int,
                      end_to_end_duration: Int,
                      alignment_buffered: Int,
                      processed_data: Int,
                      persisted_data: Int,
                      num_subtasks: Int,
                      num_acknowledged_subtasks: Int,
                      checkpoint_type: String,
                      tasks: Map[String, Task],
                      external_path: String,
                      discarded: Boolean
                    )

case class Counts(restored: Int, total: Int, in_progress: Int, completed: Int, failed: Int)

case class Summary(
                    state_size: MetricSummary,
                    end_to_end_duration: MetricSummary,
                    alignment_buffered: MetricSummary,
                    processed_data: MetricSummary,
                    persisted_data: MetricSummary
                  )

case class MetricSummary(min: Double, max: Double, avg: Double)

case class Latest(completed: Option[Completed], savepoint: Option[Savepoint], failed: Option[Failed], restored: Option[Restored])

case class Task(status: String, metrics: Map[String, Metric])

case class Metric(name: String, value: Double)

case class Savepoint(id: Int, status: String, trigger_timestamp: Long, external_path: String)

case class Failed(id: Int, status: String, trigger_timestamp: Long, failure_timestamp: Long, failure_message: String)

case class Restored(id: Int, status: String, trigger_timestamp: Long, external_path: String, restore_timestamp: Long)

object CheckpointJsonProtocol extends DefaultJsonProtocol {
  implicit val metricFormat: RootJsonFormat[Metric] = jsonFormat2(Metric)
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat2(Task)
  implicit val savepointFormat: RootJsonFormat[Savepoint] = jsonFormat4(Savepoint)
  implicit val failedFormat: RootJsonFormat[Failed] = jsonFormat5(Failed)
  implicit val restoredFormat: RootJsonFormat[Restored] = jsonFormat5(Restored)
  implicit val countsFormat: RootJsonFormat[Counts] = jsonFormat5(Counts)
  implicit val metricSummaryFormat: RootJsonFormat[MetricSummary] = jsonFormat3(MetricSummary)
  implicit val summaryFormat: RootJsonFormat[Summary] = jsonFormat5(Summary)
  implicit val completedFormat: RootJsonFormat[Completed] = jsonFormat17(Completed)
  implicit val latestFormat: RootJsonFormat[Latest] = jsonFormat4(Latest)
  implicit val checkpointFormat: RootJsonFormat[Checkpoint] = jsonFormat4(Checkpoint.apply)
}