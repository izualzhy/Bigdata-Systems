package cn.izualzhy

import org.apache.calcite.sql.{SqlKind, SqlSelect}
import org.apache.calcite.sql.parser.SqlParser

object SqlParserExample extends App {
//  val sql = "SELECT * FROM emps WHERE id = 1"
//  val sql = "SELECT Row('literal_column', a, b, c, d, 'another_literal_column') FROM source_table"
//  val sql = "SELECT Row(a,'literal_column', 'another_literal_column') FROM source_table"
  val sql = "SELECT Row(a, b, c, d, e, f, 'literal_column', 'another_literal_column') FROM source_table"
//  val sql = "SELECT concat(a, 'literal_column', 'another_literal_column') FROM source_table"
  val sqlParser = SqlParser.create(sql)

  val sqlNode = sqlParser.parseQuery()


  sqlNode.getKind match {
    case SqlKind.SELECT => println(s"from:${sqlNode.asInstanceOf[SqlSelect].getFrom}")
    case _ => println("to be continued.")
  }
}
