package cn.izualzhy

import org.apache.calcite.sql.parser.SqlParser

object JustTest extends App {
//  val sql = "SELECT Row('literal_column', a) FROM source_table;"
  val sql =
    """
      |CREATE TABLE employee_information (
      |    emp_id INT,
      |    name VARCHAR,
      |    dept_id INT
      |) WITH (
      |    'connector' = 'filesystem',
      |    'path' = '/path/to/something.csv',
      |    'format' = 'csv'
      |);""".stripMargin
  val sqlParser = SqlParser.create(sql)

  val sqlNode = sqlParser.parseQuery()
//  val sqlNode = sqlParser.parseStmtList()
  println(sqlNode)
}
