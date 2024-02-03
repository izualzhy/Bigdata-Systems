package cn.izualzhy

import org.apache.calcite.jdbc.JavaTypeFactoryImpl
import org.apache.calcite.rel.`type`.RelDataType
import org.apache.calcite.sql.`type`.SqlTypeName
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.util.Pair
import scala.collection.JavaConverters._

import java.util

object JustTest extends App {
  val i = "date : 2024-02-02 16:19:55"

  println(i.substring(i.indexOf(":") + 1))

}
