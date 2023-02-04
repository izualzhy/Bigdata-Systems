package cn.izualzhy

import java.util
import java.util.{List, Properties}

import org.apache.calcite.config.CalciteConnectionConfigImpl
import org.apache.calcite.interpreter.Bindables
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.plan.hep.{HepPlanner, HepProgramBuilder}
import org.apache.calcite.plan.{ConventionTraitDef, RelOptCluster, RelOptTable, RelOptUtil}
import org.apache.calcite.prepare.{CalciteCatalogReader, PlannerImpl}
import org.apache.calcite.rel.{RelDistributionTraitDef, RelRoot}
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory, RelDataTypeSystem, RelDataTypeSystemImpl}
import org.apache.calcite.rel.rules.{CoreRules, ProjectMergeRule, PruneEmptyRules, ReduceExpressionsRule}
import org.apache.calcite.rex.RexBuilder
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.`type`.{BasicSqlType, SqlTypeFactoryImpl, SqlTypeName}
import org.apache.calcite.sql.fun.SqlStdOperatorTable
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.validate.{SqlValidator, SqlValidatorUtil}
import org.apache.calcite.sql2rel.{RelDecorrelator, SqlToRelConverter}
import org.apache.calcite.tools.Frameworks

import scala.collection.JavaConverters._

/**
 * Author: yingshin
 * Date: 2022/4/12 18:56
 * Package: cn.izualzhy
 * Description:
 *
 */
object CalcitePlanExample extends App {
  /*
  val sql =
    """
      |select u.id as user_id, u.name as user_name, j.company as user_company, u.age as user_age
      |from users u join jobs j on u.name=j.name
      |where u.age > 30 and j.id>10
      |order by user_id""".stripMargin
   */
  val sql =
    """
      |select u.id as user_id, u.name as user_name, j.company as user_company, u.age as user_age
      |from users u join jobs j on u.id=j.id
      |where u.age > 30 and j.id>10
      |order by user_id""".stripMargin

  val rootSchema = Frameworks.createRootSchema(true)
  rootSchema.add("USERS", new AbstractTable {
    override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
      typeFactory.builder()
        .add("ID", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.INTEGER))
        .add("NAME", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.CHAR))
        .add("AGE", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.INTEGER))
        .build()
    }
  })

  rootSchema.add("JOBS", new AbstractTable {
    override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
      typeFactory.builder()
        .add("ID", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.INTEGER))
        .add("NAME", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.CHAR))
        .add("COMPANY", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.CHAR))
        .build()
    }
  })

  // parse
  val parser = SqlParser.create(sql, SqlParser.config())
  val parsedNode = parser.parseStmt()

  // validate
  val sqlTypeFactory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT)
  val calciteCatalogReader = new CalciteCatalogReader(
    CalciteSchema.from(rootSchema),
    CalciteSchema.from(rootSchema).path(null),
    sqlTypeFactory,
    new CalciteConnectionConfigImpl(new Properties())
  )
  val validator = SqlValidatorUtil.newValidator(
    SqlStdOperatorTable.instance(),
    calciteCatalogReader,
    sqlTypeFactory,
    SqlValidator.Config.DEFAULT)
  val validatedNode = validator.validate(parsedNode)
  println(validatedNode)

  // 语义分析：SqlNode–>RelNode/RexNode
  val rexBuilder = new RexBuilder(sqlTypeFactory)
  val hepProgramBuilder = new HepProgramBuilder()
  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_INTO_JOIN)

  val hepPlanner = new HepPlanner(hepProgramBuilder.build())

  val relOptCluster = RelOptCluster.create(hepPlanner, rexBuilder)
  val frameworkConfig = Frameworks.newConfigBuilder()
    .parserConfig(SqlParser.Config.DEFAULT)
    .defaultSchema(rootSchema)
    .traitDefs(ConventionTraitDef.INSTANCE, RelDistributionTraitDef.INSTANCE)
    .build()
  val sqlToRelConverterConfig = frameworkConfig.getSqlToRelConverterConfig()
    .withTrimUnusedFields(false)

  val sqlToRelConverter = new SqlToRelConverter(
    new PlannerImpl(frameworkConfig),
    validator,
    calciteCatalogReader,
    relOptCluster,
    frameworkConfig.getConvertletTable,
    sqlToRelConverterConfig)
  var relRoot = sqlToRelConverter.convertQuery(validatedNode, true, true)
  relRoot = relRoot.withRel(sqlToRelConverter.flattenTypes(relRoot.rel, true))
  val relBuilder = sqlToRelConverterConfig.getRelBuilderFactory.create(relOptCluster, null)
  relRoot = relRoot.withRel(RelDecorrelator.decorrelateQuery(relRoot.rel, relBuilder))
  var relNode = relRoot.project()
  println(RelOptUtil.toString(relNode))

  // optimize: RelNode -> RelNode
  hepPlanner.setRoot(relNode)
  relNode = hepPlanner.findBestExp()
  println(RelOptUtil.toString(relNode))
}
