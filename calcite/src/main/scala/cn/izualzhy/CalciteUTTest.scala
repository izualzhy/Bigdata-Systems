package cn.izualzhy

import java.util
import java.util.Properties

import org.apache.calcite.config.CalciteConnectionConfigImpl
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.plan.{ConventionTraitDef, RelOptCluster, RelOptTable, RelOptUtil}
import org.apache.calcite.plan.hep.{HepPlanner, HepProgramBuilder}
import org.apache.calcite.prepare.CalciteCatalogReader
import org.apache.calcite.rel.{RelDistributionTraitDef, RelRoot}
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory, RelDataTypeSystem, RelDataTypeSystemImpl}
import org.apache.calcite.rel.rules.CoreRules
import org.apache.calcite.rex.RexBuilder
import org.apache.calcite.schema.impl.AbstractTable
import org.apache.calcite.sql.`type`.{BasicSqlType, SqlTypeFactoryImpl, SqlTypeName}
import org.apache.calcite.sql.fun.SqlStdOperatorTable
import org.apache.calcite.sql.parser.SqlParser
import org.apache.calcite.sql.validate.{SqlValidator, SqlValidatorUtil}
import org.apache.calcite.sql2rel.{RelDecorrelator, SqlToRelConverter}
import org.apache.calcite.tools.Frameworks

/**
 * Author: yingshin
 * Date: 2022/4/18 12:48
 * Package: cn.izualzhy
 * Description:
 *
 */
object CalciteUTTest extends App {
  val sql =
    """
      |select d.deptno as a, d.name as b, d.job as c, e.eptno as aa, e.name as bb from dept d join emp e
      |on d.name = e.name
      |where d.deptno > 100 and e.eptno > 100
      |order by c""".stripMargin

  val rootSchema = Frameworks.createRootSchema(true)
  rootSchema.add("DEPT", new AbstractTable {
    override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
      typeFactory.builder()
        .add("DEPTNO", SqlTypeName.INTEGER)
        .add("NAME", SqlTypeName.VARCHAR)
        .add("JOB", SqlTypeName.VARCHAR)
        .build()
    }
  })

  rootSchema.add("EMP", new AbstractTable {
    override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
      typeFactory.builder()
        .add("EPTNO", SqlTypeName.INTEGER)
        .add("DEPTNO", SqlTypeName.INTEGER)
        .add("NAME", SqlTypeName.VARCHAR)
        .build()
    }
  })
  /*
  rootSchema.add("DEPT", new AbstractTable {
    override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
      typeFactory.builder()
        .add("DEPTNO", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.INTEGER))
        .add("NAME", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.CHAR))
        .add("JOB", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.CHAR))
        .build()
    }
  })

  rootSchema.add("EMP", new AbstractTable {
    override def getRowType(typeFactory: RelDataTypeFactory): RelDataType = {
      typeFactory.builder()
        .add("EPTNO", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.INTEGER))
        .add("DEPTNO", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.INTEGER))
        .add("NAME", new BasicSqlType(new RelDataTypeSystemImpl {}, SqlTypeName.CHAR))
        .build()
    }
  })
   */

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
  //  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_INTO_JOIN)
  //  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_CORRELATE)
  //  hepProgramBuilder.addRuleInstance(CoreRules.JOIN_CONDITION_PUSH)
  //  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_MERGE)
  //  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_MULTI_JOIN_MERGE)
  //  hepProgramBuilder.addRuleInstance(CoreRules.JOIN_EXTRACT_FILTER)
  //  hepProgramBuilder.addRuleInstance(CoreRules.AGGREGATE_REMOVE)
  //  hepProgramBuilder.addRuleInstance(CoreRules.PROJECT_FILTER_TRANSPOSE);
  //  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_PROJECT_TRANSPOSE)
  //  hepProgramBuilder.addRuleInstance(CoreRules.CALC_REDUCE_EXPRESSIONS)
  //  hepProgramBuilder.addRuleInstance(PruneEmptyRules.PROJECT_INSTANCE)
  //  hepProgramBuilder.addRuleInstance(CoreRules.FILTER_REDUCE_EXPRESSIONS)
  //  hepProgramBuilder.addRuleInstance(CoreRules.PROJECT_REDUCE_EXPRESSIONS)
  //  hepProgramBuilder.addRuleInstance(CoreRules.JOIN_REDUCE_EXPRESSIONS)
  //  hepProgramBuilder.addRuleInstance(CoreRules.WINDOW_REDUCE_EXPRESSIONS)
  hepProgramBuilder
    .addRuleInstance(CoreRules.FILTER_INTO_JOIN)
    .addRuleInstance(CoreRules.FILTER_MERGE)
    .addRuleInstance(CoreRules.JOIN_CONDITION_PUSH)
    .addRuleInstance(CoreRules.FILTER_PROJECT_TRANSPOSE)
    .addRuleInstance(CoreRules.FILTER_SET_OP_TRANSPOSE)
    .addRuleInstance(CoreRules.JOIN_PUSH_TRANSITIVE_PREDICATES)

  val hepPlanner = new HepPlanner(hepProgramBuilder.build())

  val relOptCluster = RelOptCluster.create(hepPlanner, rexBuilder)
  val frameworkConfig = Frameworks.newConfigBuilder()
    .parserConfig(SqlParser.Config.DEFAULT)
    .defaultSchema(rootSchema)
    .traitDefs(ConventionTraitDef.INSTANCE, RelDistributionTraitDef.INSTANCE)
    .build()
  val sqlToRelConverterConfig = frameworkConfig.getSqlToRelConverterConfig()
    .withTrimUnusedFields(false)

  class ViewExpanderImpl() extends RelOptTable.ViewExpander {
    override def expandView(rowType: RelDataType, queryString: String, schemaPath: util.List[String], viewPath: util.List[String]): RelRoot = null
  }

  val sqlToRelConverter = new SqlToRelConverter(
    new ViewExpanderImpl,
    //    new PlannerImpl(frameworkConfig),
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
