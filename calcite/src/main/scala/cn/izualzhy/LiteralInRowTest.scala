package cn.izualzhy

import org.apache.calcite.sql.parser.SqlParser

object LiteralInRowTest extends App {
  val sql = "SELECT Row(f0, 'literal_column') FROM source_table"
//  val sql = "SELECT Row('literal_column', f0) FROM source_table"
  val sqlParser = SqlParser.create(sql)

  val sqlNode = sqlParser.parseQuery()
}
