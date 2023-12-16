package cn.izualzhy

import org.apache.calcite.adapter.csv.{CsvSchema, CsvTable}
import org.apache.calcite.adapter.enumerable.EnumerableConvention
import org.apache.calcite.plan.RelOptUtil
import org.apache.calcite.rel.core.JoinRelType
import org.apache.calcite.sql.fun.SqlStdOperatorTable
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.tools.{Frameworks, RelBuilder, RelRunners}

import java.io.File

object RelBuilderExample extends App {
  val rootSchema = Frameworks.createRootSchema(true)
  val csvPath = getClass.getClassLoader.getResource("book_author").getPath
  val csvSchema = new CsvSchema(new File(csvPath.toString), CsvTable.Flavor.SCANNABLE)
  rootSchema.add("book", csvSchema.getTable("book"))
  rootSchema.add("author", csvSchema.getTable("author"))

  val frameworkConfig = Frameworks.newConfigBuilder()
    .parserConfig(SqlParser.Config.DEFAULT)
    .defaultSchema(rootSchema)
    .build()
  val relBuilder = RelBuilder.create(frameworkConfig)

  val node = relBuilder
//    .adoptConvention(EnumerableConvention.INSTANCE)
    .scan("book")
    .scan("author")
    .join(JoinRelType.INNER, "id")
    .filter(
      relBuilder.call(SqlStdOperatorTable.GREATER_THAN,
        relBuilder.field("publish_year"),
        relBuilder.literal(2018)))
    .project(
      relBuilder.field("id"),
      relBuilder.field("title"),
      relBuilder.field("publish_year"),
      relBuilder.field("lname"),
      relBuilder.field("fname"))
    .sort(relBuilder.field("id"))
    .build()

  println(RelOptUtil.toString(node))

  val preparedStatement = RelRunners.run(node)
  val resultSet = preparedStatement.executeQuery()

  private val columnCount = resultSet.getMetaData.getColumnCount
  val columnNames = (1 to columnCount)
    .map(resultSet.getMetaData.getColumnName(_))
  while (resultSet.next()) {
    val values = (1 to columnCount)
      .map(resultSet.getObject(_).toString)
    println((columnNames zip values).map { case (x, y) => s"$x:$y" }.mkString(" "))
  }

}
