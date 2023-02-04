package cn.izualzhy

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

/**
 * Author: izualzhy
 * Date: 2022/7/23 17:16
 * Package: cn.izualzhy
 * Description:
 *
 */

object PreDefinedWindowSQLSample extends App {
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setParallelism(1)
  val tEnv = StreamTableEnvironment.create(env)
  val userClicksCsvPath = Thread.currentThread().getContextClassLoader.getResource("user_clicks.csv").getPath

  val table_user_clicks =
    s"""
      |CREATE TEMPORARY TABLE user_clicks (
      |  username VARCHAR,
      |  click_url VARCHAR,
      |  ts TIMESTAMP(3),
      |  WATERMARK FOR ts AS ts - INTERVAL '2' SECOND
      |) with (
      | 'connector' = 'filesystem',
      | 'path' = '${userClicksCsvPath}',
      | 'format' = 'csv'
      |)""".stripMargin

  val table_window_output =
    """
      |CREATE TEMPORARY TABLE window_output (
      |  window_start TIMESTAMP,
      |  window_end TIMESTAMP,
      |  username VARCHAR,
      |  clicks BIGINT
      |) with (
      |  'connector'='print'
      |);""".stripMargin

  val calc_tumble_sql =
    """
      |INSERT INTO
      |    window_output
      |SELECT
      |    TUMBLE_START (ts, INTERVAL '7' SECOND),
      |    TUMBLE_END (ts, INTERVAL '7' SECOND),
      |    username,
      |    COUNT (click_url)
      |FROM
      |    user_clicks
      |GROUP BY
      |    TUMBLE (ts, INTERVAL '7' SECOND), username
      |""".stripMargin

  val calc_hop_sql =
    """
      |INSERT INTO
      |    window_output
      |SELECT
      |    HOP_START (ts, INTERVAL '2' SECOND, INTERVAL '5' SECOND),
      |    HOP_END (ts, INTERVAL '2' SECOND, INTERVAL '5' SECOND),
      |    username,
      |    COUNT (click_url)
      |FROM
      |    user_clicks
      |GROUP BY
      |    HOP (ts, INTERVAL '2' SECOND, INTERVAL '5' SECOND), username
      |""".stripMargin

  tEnv.executeSql(table_user_clicks)
  tEnv.executeSql(table_window_output)
  tEnv.executeSql(calc_hop_sql)
//  tEnv.executeSql(calc_tumble_sql)
}
