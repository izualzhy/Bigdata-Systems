package cn.izualzhy
import org.apache.calcite.config.CalciteConnectionConfigImpl
import org.apache.calcite.jdbc.CalciteSchema
import org.apache.calcite.plan.{ConventionTraitDef, RelOptCluster, RelOptUtil}
import org.apache.calcite.plan.hep.{HepPlanner, HepProgramBuilder}
import org.apache.calcite.prepare.{CalciteCatalogReader, PlannerImpl}
import org.apache.calcite.rel.RelDistributionTraitDef
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
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeHint, TypeInformation}
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.table.calcite.CalciteParser
import org.slf4j.{Logger, LoggerFactory}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.util._

object FlinkOnK8STest extends App {
  val logger = LoggerFactory.getLogger("FlinkOnK8sTest");
  val env = StreamExecutionEnvironment.getExecutionEnvironment;


  args.foreach(s => logger.info(s"arg : ${s}"))
  args.foreach(s => println(s"arg : ${s}"))

//  val calciteParser = new CalciteParser(SqlParser.Config.DEFAULT)
//  val sqlNode = calciteParser.parse("select * from test_table")
//  logger.info(s"sqlNode:${sqlNode}")

  val currentDate = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss"));

  val fsStateBackend = new FsStateBackend(args(7))
  env.setStateBackend(fsStateBackend);
  val checkpointConfig = env.getCheckpointConfig
  checkpointConfig.setCheckpointInterval(args(3).toInt)
  checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
  checkpointConfig.setCheckpointTimeout(30000)
  checkpointConfig.setTolerableCheckpointFailureNumber(5)

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", args(2))
  val groupId = "zy_test_" + args(4)
  val topic = args(5)
  val sinkTopic = args(6)
  properties.setProperty("group.id", groupId)

  args.foreach(i => println(s"args:${i}"))

  val consumer = new FlinkKafkaConsumer[String](topic, new SimpleStringSchema(), properties).setStartFromLatest()

  val sourceStream = env.addSource(consumer)
  sourceStream
    .map(i => Try {
      val elems = i.split(" ")
      (elems(0), elems(1).toInt)
    } match {
      case Success(value) => value
      case Failure(exception) => (exception.toString, -1)
    }).addSink(new BufferingSink)

  env.execute(this.getClass.getName)
  test()

  class BufferingSink() extends SinkFunction[(String, Int)] with CheckpointedFunction {
    val logger = LoggerFactory.getLogger("BufferingSink")
    @transient
    private var checkPointedState: ListState[(String, Int)] = _
//    private val bufferedElements = ListBuffer[(String, Int)]()

    override def invoke(value: (String, Int), context: SinkFunction.Context): Unit = {
//      if (value._2 > 0) {
//        bufferedElements.append(value)
//      }
      println(s"receive value:${value}")
      logger.info(s"receive value:${value}")

      if (value._2 == 123456789) {
        throw new RuntimeException("get a number. quit.")
      }
    }

    override def snapshotState(context: FunctionSnapshotContext): Unit = {
//      checkPointedState.clear()
//      for (element <- bufferedElements) {
//        checkPointedState.add(element)
//      }
    }

    override def initializeState(context: FunctionInitializationContext): Unit = {
//      val descriptor = new ListStateDescriptor[(String, Int)](
//        "buffered-elements",
//        TypeInformation.of(new TypeHint[(String, Int)]() {})
//      )
//
//      checkPointedState = context.getOperatorStateStore.getListState(descriptor);
//      if (context.isRestored) {
//        println(s"initializeState checkPointedStated.size:${checkPointedState.get().asScala.size}")
//        checkPointedState.get().asScala.foreach(i => {
//          println(s"state: (${i._1.length}, ${i._2})")
//        })
//      }
    }
  }

  def test() = {
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
}
