package cn.izualzhy.blog

import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.`type`.SqlTypeName
import scala.collection.JavaConverters._

import java.util

class BlogAbstractTable extends AbstractTable {
  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
    val columnNames = List("URL", "TITLE", "PUB_DATE", "TAGS")
    val columnTypes = List(
      typeFactory.createSqlType(SqlTypeName.VARCHAR),
      typeFactory.createSqlType(SqlTypeName.VARCHAR),
      typeFactory.createSqlType(SqlTypeName.VARCHAR),
      typeFactory.createSqlType(SqlTypeName.VARCHAR))

    typeFactory.createStructType(columnTypes.asJava, columnNames.asJava)
  }
}
