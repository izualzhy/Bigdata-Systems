package cn.izualzhy

import org.apache.calcite.jdbc.JavaTypeFactoryImpl
import org.apache.calcite.rel.`type`.RelDataType
import org.apache.calcite.sql.`type`.SqlTypeName
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.util.Pair
import scala.collection.JavaConverters._

import java.util

object JustTest extends App {
//  val sql = "SELECT Row('literal_column', a) FROM source_table;"
//  val sql =
//    """
//      |CREATE TABLE employee_information (
//      |    emp_id INT,
//      |    name VARCHAR,
//      |    dept_id INT
//      |) WITH (
//      |    'connector' = 'filesystem',
//      |    'path' = '/path/to/something.csv',
//      |    'format' = 'csv'
//      |);""".stripMargin
//  val sqlParser = SqlParser.create(sql)

//  val sqlNode = sqlParser.parseQuery()
//  val sqlNode = sqlParser.parseStmtList()
//  println(sqlNode)

  val sqlTypeFactory = new JavaTypeFactoryImpl()
  val relDataType1 = sqlTypeFactory.createSqlType(SqlTypeName.TIMESTAMP)
  println(relDataType1)

  val relDataType2 = sqlTypeFactory.createStructType(
    Pair.zip(List("line").asJava,List(sqlTypeFactory.createSqlType(SqlTypeName.VARCHAR)).asJava)
  )
  println(relDataType2)
}
