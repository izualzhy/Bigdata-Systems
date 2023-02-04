package cn.izualzhy

import java.io.File

import org.apache.calcite.adapter.csv.{CsvSchema, CsvTable}
import org.apache.calcite.adapter.enumerable.EnumerableConvention
import org.apache.calcite.plan.RelOptUtil
import org.apache.calcite.rel.core.JoinRelType
import org.apache.calcite.sql.fun.SqlStdOperatorTable
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.tools.{Frameworks, RelBuilder}

object AlgebraTest extends App {
  val rootSchema = Frameworks.createRootSchema(true)
  val csvPath = getClass.getClassLoader.getResource("book_author").getPath
  val csvSchema = new CsvSchema(new File(csvPath.toString), CsvTable.Flavor.SCANNABLE)
  rootSchema.add("author", csvSchema.getTable("author"))
  rootSchema.add("book", csvSchema.getTable("book"))

  val frameworkConfig = Frameworks.newConfigBuilder()
    .parserConfig(SqlParser.Config.DEFAULT)
    .defaultSchema(rootSchema)
    .build()
  val relBuilder = RelBuilder.create(frameworkConfig)

  val node = relBuilder
    .adoptConvention(EnumerableConvention.INSTANCE)
    .scan("book")
    .scan("author")
    .join(JoinRelType.LEFT, "id")
    .filter(
      relBuilder.call(SqlStdOperatorTable.GREATER_THAN,
      relBuilder.field("publish_year"),
      relBuilder.literal(1830)))
    .project(
      relBuilder.field("id"),
      relBuilder.field("title"),
      relBuilder.field("publish_year"),
      relBuilder.field("lname"),
      relBuilder.field("fname"))
    .sort(relBuilder.field("id"))
    .build()

  println(RelOptUtil.toString(node))
}
