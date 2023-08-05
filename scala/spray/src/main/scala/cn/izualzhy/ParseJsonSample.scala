package cn.izualzhy


// 导入解析 JSON 的格式
import spray.json._
import DefaultJsonProtocol._
import CheckpointJsonProtocol._

object ParseJsonSample extends App {
  // 定义要解析的 JSON 字符串
  val jsonString =
    """
      |{
      |    "counts": {
      |        "restored": 0,
      |        "total": 2,
      |        "in_progress": 0,
      |        "completed": 2,
      |        "failed": 0
      |    },
      |    "summary": {
      |        "state_size": {
      |            "min": 1050285,
      |            "max": 1050285,
      |            "avg": 1050285
      |        },
      |        "end_to_end_duration": {
      |            "min": 238,
      |            "max": 413,
      |            "avg": 325
      |        },
      |        "alignment_buffered": {
      |            "min": 0,
      |            "max": 0,
      |            "avg": 0
      |        },
      |        "processed_data": {
      |            "min": 0,
      |            "max": 0,
      |            "avg": 0
      |        },
      |        "persisted_data": {
      |            "min": 0,
      |            "max": 0,
      |            "avg": 0
      |        }
      |    },
      |    "latest": {
      |        "completed": {
      |            "@class": "completed",
      |            "id": 2,
      |            "status": "COMPLETED",
      |            "is_savepoint": false,
      |            "trigger_timestamp": 1691052340653,
      |            "latest_ack_timestamp": 1691052340891,
      |            "state_size": 1050285,
      |            "end_to_end_duration": 238,
      |            "alignment_buffered": 0,
      |            "processed_data": 0,
      |            "persisted_data": 0,
      |            "num_subtasks": 1,
      |            "num_acknowledged_subtasks": 1,
      |            "checkpoint_type": "CHECKPOINT",
      |            "tasks": {},
      |            "external_path": "oss://zyb-flinktest.oss-cn-beijing.aliyuncs.com/flink/checkpoints/cola_zhangying14_checkpoint_on_oss/ff7c7bd965ca1ec6bcfd407ad6836ad0/chk-2",
      |            "discarded": false
      |        },
      |        "savepoint": null,
      |        "failed": null,
      |        "restored": null
      |    },
      |    "history": [
      |        {
      |            "@class": "completed",
      |            "id": 2,
      |            "status": "COMPLETED",
      |            "is_savepoint": false,
      |            "trigger_timestamp": 1691052340653,
      |            "latest_ack_timestamp": 1691052340891,
      |            "state_size": 1050285,
      |            "end_to_end_duration": 238,
      |            "alignment_buffered": 0,
      |            "processed_data": 0,
      |            "persisted_data": 0,
      |            "num_subtasks": 1,
      |            "num_acknowledged_subtasks": 1,
      |            "checkpoint_type": "CHECKPOINT",
      |            "tasks": {},
      |            "external_path": "oss://zyb-flinktest.oss-cn-beijing.aliyuncs.com/flink/checkpoints/cola_zhangying14_checkpoint_on_oss/ff7c7bd965ca1ec6bcfd407ad6836ad0/chk-2",
      |            "discarded": false
      |        },
      |        {
      |            "@class": "completed",
      |            "id": 1,
      |            "status": "COMPLETED",
      |            "is_savepoint": false,
      |            "trigger_timestamp": 1691052280654,
      |            "latest_ack_timestamp": 1691052281067,
      |            "state_size": 1050285,
      |            "end_to_end_duration": 413,
      |            "alignment_buffered": 0,
      |            "processed_data": 0,
      |            "persisted_data": 0,
      |            "num_subtasks": 1,
      |            "num_acknowledged_subtasks": 1,
      |            "checkpoint_type": "CHECKPOINT",
      |            "tasks": {},
      |            "external_path": "oss://zyb-flinktest.oss-cn-beijing.aliyuncs.com/flink/checkpoints/cola_zhangying14_checkpoint_on_oss/ff7c7bd965ca1ec6bcfd407ad6836ad0/chk-1",
      |            "discarded": false
      |        }
      |    ]
      |}""".stripMargin

  val checkpoint = jsonString.parseJson.convertTo[Checkpoint](CheckpointJsonProtocol.checkpointFormat)
  println(checkpoint)
  println(checkpoint.history.map(_.trigger_timestamp))
}
