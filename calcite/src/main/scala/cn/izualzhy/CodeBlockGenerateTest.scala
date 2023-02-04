package cn.izualzhy

import java.io.File
import java.util

import org.apache.calcite.adapter.csv.{CsvSchema, CsvTable}
import org.apache.calcite.adapter.enumerable.{EnumerableConvention, EnumerableRel, EnumerableRelImplementor}
import org.apache.calcite.linq4j.tree.Expressions
import org.apache.calcite.plan.RelOptUtil
import org.apache.calcite.runtime.Utilities
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.tools.{Frameworks, RelBuilder}
import org.codehaus.commons.compiler.CompilerFactoryFactory

object CodeBlockGenerateTest extends App {
  val rootSchema = Frameworks.createRootSchema(true)
  val csvPath = getClass.getClassLoader.getResource("book_author").getPath
  val csvSchema = new CsvSchema(new File(csvPath.toString), CsvTable.Flavor.TRANSLATABLE)
//  val csvSchema = new CsvSchema(new File(csvPath.toString), CsvTable.Flavor.SCANNABLE)
  rootSchema.add("book", csvSchema.getTable("book"))

  val frameworkConfig = Frameworks.newConfigBuilder()
    .parserConfig(SqlParser.Config.DEFAULT)
    .defaultSchema(rootSchema)
    .build()
  val relBuilder = RelBuilder.create(frameworkConfig)

  val node = relBuilder
    .adoptConvention(EnumerableConvention.INSTANCE)
    .scan("book")
    .build()

  println(RelOptUtil.toString(node))

  val relImplementor: EnumerableRelImplementor = new EnumerableRelImplementor(node.getCluster.getRexBuilder, new util.HashMap[String, AnyRef])
  val classExpr = relImplementor.implementRoot(node.asInstanceOf[EnumerableRel], EnumerableRel.Prefer.ARRAY)
  val javaCode = Expressions.toString(classExpr.memberDeclarations, "\n", false)
  println(javaCode)

  val compilerFactory = CompilerFactoryFactory.getDefaultCompilerFactory
  val cbe = compilerFactory.newClassBodyEvaluator
  cbe.setClassName(classExpr.name)
  cbe.setExtendedClass(classOf[Utilities])
}
