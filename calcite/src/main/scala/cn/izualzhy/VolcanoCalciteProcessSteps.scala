package cn.izualzhy

import java.io.File
import java.util
import java.util.Properties
import org.apache.calcite.adapter.csv.{CsvSchema, CsvTable}
import org.apache.calcite.adapter.enumerable.{EnumerableRel, EnumerableRelImplementor}
import org.apache.calcite.config.{CalciteConnectionConfigImpl, CalciteConnectionProperty}
import org.apache.calcite.jdbc.{CalciteSchema, JavaTypeFactoryImpl}
import org.apache.calcite.linq4j.tree.Expressions
import org.apache.calcite.plan.RelOptTable.ViewExpander
import org.apache.calcite.plan.hep.{HepPlanner, HepProgramBuilder}
import org.apache.calcite.plan.volcano.VolcanoPlanner
import org.apache.calcite.plan.{ConventionTraitDef, RelOptCluster, RelOptUtil, RelTraitSet}
import org.apache.calcite.prepare.CalciteCatalogReader
import org.apache.calcite.rel.{RelCollationTraitDef, RelDistributionTraitDef, RelRoot}
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeSystem}
import org.apache.calcite.rel.rules.CoreRules
import org.apache.calcite.rex.RexBuilder
import org.apache.calcite.sql.`type`.SqlTypeFactoryImpl
import org.apache.calcite.sql.fun.SqlStdOperatorTable
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.validate.{SqlValidator, SqlValidatorUtil}
import org.apache.calcite.sql.{SqlExplainFormat, SqlExplainLevel}
import org.apache.calcite.sql2rel.{SqlToRelConverter, StandardConvertletTable}
import org.apache.calcite.tools.{Frameworks, RelRunners}

import java.sql.PreparedStatement

object VolcanoCalciteProcessSteps extends App {
  val query =
    """SELECT b.id,
      |         b.title,
      |         b.publish_year,
      |         a.fname,
      |         a.lname
      |FROM Book AS b
      |INNER JOIN Author a
      |    ON b.author_id = a.id
      |WHERE b.publish_year > 2018
      |ORDER BY b.id LIMIT 5
      |""".stripMargin
  // parse: sql -> SqlNode
  val sqlParser = SqlParser.create(query)
  val sqlNodeParsed = sqlParser.parseQuery()
  println(s"[Parsed query]\n${sqlNodeParsed}")

  // 参考第一篇笔记里的 CsvSchema 初始化 author book 表，resources/book_author 目录
  val rootSchema = Frameworks.createRootSchema(true)
  val csvPath = getClass.getClassLoader.getResource("book_author").getPath
  val csvSchema = new CsvSchema(new File(csvPath.toString), CsvTable.Flavor.SCANNABLE)
  rootSchema.add("author", csvSchema.getTable("author"))
  rootSchema.add("book", csvSchema.getTable("book"))

  val sqlTypeFactory = new JavaTypeFactoryImpl()
  //  val sqlTypeFactory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT)
  val properties = new Properties()
  properties.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), "false")
  // reader 接收 schema，用于检测字段名、字段类型、表名等是否存在和一致
  val catalogReader = new CalciteCatalogReader(
    CalciteSchema.from(rootSchema),
    CalciteSchema.from(rootSchema).path(null),
    sqlTypeFactory,
    new CalciteConnectionConfigImpl(properties))
  // 简单示例，大部分参数采用默认值即可
  val validator = SqlValidatorUtil.newValidator(
    SqlStdOperatorTable.instance(),
    catalogReader,
    sqlTypeFactory,
    SqlValidator.Config.DEFAULT)
  // validate: SqlNode -> SqlNode
  val sqlNodeValidated = validator.validate(sqlNodeParsed)
  println(s"[Validated query]\n${sqlNodeValidated}")

  val volcanoPlanner = new VolcanoPlanner();
  volcanoPlanner.addRelTraitDef(ConventionTraitDef.INSTANCE)
  volcanoPlanner.addRelTraitDef(RelCollationTraitDef.INSTANCE)
  volcanoPlanner.setNoneConventionHasInfiniteCost(false)
  val rexBuilder = new RexBuilder(sqlTypeFactory)
//  val hepProgramBuilder = new HepProgramBuilder()
//  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_INTO_JOIN)
//  val hepPlanner = new HepPlanner(hepProgramBuilder.build()) {
//    override def emptyTraitSet(): RelTraitSet = {
//      var traitSet = super.emptyTraitSet
//      //      traitSet = traitSet.plus(ConventionTraitDef.INSTANCE.getDefault)
//      //      traitSet = traitSet.plus(RelCollationTraitDef.INSTANCE.getDefault)
//
//      traitSet
//    }
//  }

  val relOptCluster = RelOptCluster.create(volcanoPlanner, rexBuilder)
  val sqlToRelConverter = new SqlToRelConverter(
    // 没有使用 view
    new ViewExpander {
      override def expandView(rowType: RelDataType, queryString: String, schemaPath: util.List[String], viewPath: util.List[String]): RelRoot = null
    },
    validator,
    catalogReader,
    relOptCluster,
    // 均使用标准定义即可
    StandardConvertletTable.INSTANCE,
    SqlToRelConverter.config())
  var logicalPlan = sqlToRelConverter.convertQuery(sqlNodeValidated, true, true).rel
  println(RelOptUtil.dumpPlan("[Logical plan]", logicalPlan, SqlExplainFormat.TEXT, SqlExplainLevel.NON_COST_ATTRIBUTES))

  // Start the optimization process to obtain the most efficient physical plan based on the
  // provided rule set.
  volcanoPlanner.setRoot(logicalPlan)
  val phyPlan = volcanoPlanner.findBestExp()
  println(RelOptUtil.dumpPlan("[Physical plan]", phyPlan, SqlExplainFormat.TEXT, SqlExplainLevel.NON_COST_ATTRIBUTES))

  val preparedStatement = RelRunners.run(phyPlan)
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
