package cn.izualzhy

import com.typesafe.config.ConfigFactory
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.basic.DatabasePublisher
import slick.lifted.Tag

import java.sql.Timestamp
import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import slick.lifted.Tag

import java.io.File
import java.util.Date

object InitCDB extends App {
  case class StateBucketRuleDO(id: Option[Long],
                               cluster: String,
                               queue: String,
                               taskConfId: Long,
                               objectStorageService: String,
                               endpoint: String,
                               accessKeyId: String,
                               accessKeySecret: String,
                               createdAt: Date,
                               updatedAt: Date)
  class StateBucketRuleTable(_tableTag: Tag) extends Table[StateBucketRuleDO](_tableTag, _tableName = "t_state_bucket_rule") {
    val id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val cluster = column[String]("cluster", O.SqlType("VARCHAR(64)"))
    val queue = column[String]("queue", O.SqlType("VARCHAR(64)"))
    val taskConfId = column[Long]("task_conf_id")
    val objectStorageService = column[String]("object_storage_service")
    val endpoint = column[String]("endpoint")
    val accessKeyId = column[String]("accesskey_id")
    val accessKeySecret = column[String]("accesskey_secret")
    val createdAt: Rep[Date] = column[Date]("created_at", O.SqlType("timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"))
    val updatedAt: Rep[Date] = column[Date]("updated_at", O.SqlType("timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"))

    override def * = (id.?, cluster, queue, taskConfId, objectStorageService, endpoint, accessKeyId, accessKeySecret, createdAt, updatedAt) <> (StateBucketRuleDO.tupled, StateBucketRuleDO.unapply)
    def idx_cluster_queue_bucket = index("idx_cluster_queue_task", (cluster, queue, taskConfId), unique = false)
  }

  val stateBucketRuleTable = TableQuery[StateBucketRuleTable]

  implicit val datetime2date: BaseColumnType[Date] = MappedColumnType.base[Date, java.sql.Timestamp](
    (d: Date) => new Timestamp(d.getTime),
    (d: java.sql.Timestamp) => new Date(d.getTime))

  def setup(): Unit = {
    val setUpAction: DBIO[Unit] = DBIO.seq(
      stateBucketRuleTable.schema.create
    )
    val setupFuture = db.run(setUpAction)
    Await.result(setupFuture, Duration.Inf)
  }


    val config = ConfigFactory.parseFile(new File("../conf/c.conf"))
    val db = Database.forConfig("dev_db", config)
//  val db = Database.forConfig("test_db")
  setup()

  val l = List(
    StateBucketRuleDO(Some(1), "a1", "*", -1, "oss", "e1", "k1", "s1", new Date(), new Date()),
    StateBucketRuleDO(Some(2), "a1", "b", -1, "oss", "e2", "k2", "s2", new Date(), new Date()),
    StateBucketRuleDO(Some(3), "a1", "c", -1, "oss", "e3", "k3", "s3", new Date(), new Date()),
    StateBucketRuleDO(Some(4), "a2", "b", -1, "oss", "e4", "k4", "s4", new Date(), new Date()),
    StateBucketRuleDO(Some(5), "a3", "*", -1, "oss", "e5", "k5", "s5", new Date(), new Date()))

  def foo(cluster: String, queue: String): Option[StateBucketRuleDO] = {
    l.map(bucket => {
      val level = (bucket.cluster, bucket.queue) match {
        case (`cluster`, `queue`) => 1
        case (`cluster`, "*") => 2
        case _ => 3
      }
//      println(level)
      (bucket, level)
    }).sortBy(_._2).map(_._1).headOption
  }

  println(foo("a1", "b"))
  println(foo("a1", "c"))
  println(foo("a1", "d"))
  println(foo("a2", "b"))
  println(foo("a2", "c"))
  println(foo("a3", "b"))
  println(foo("a3", "c"))
}
