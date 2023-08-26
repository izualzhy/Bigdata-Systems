package cn.izualzhy
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.basic.DatabasePublisher
import slick.lifted.Tag

import java.sql.Timestamp
import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global

object HelloSlick extends App {
  class SlickUserTable(tag: Tag) extends Table[(Int, String, Int, String)](tag, "t_slick_user") {
    def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def user_name = column[String]("user_name")
    def sex = column[Int]("sex")
    def note = column[String]("note")

    override def * = (id, user_name, sex, note)
  }

  val db = Database.forConfig("test_db")
  println(db)
  val slickUsers = TableQuery[SlickUserTable]

  val setUpAction: DBIO[Unit] = DBIO.seq(
//    slickUsers.schema.create,

    //    slickUsers ++= Seq(
    //      (1, "A", 1, "A_note"),
    //      (2, "B", 1, "B_note"),
    //      (3, "C", 1, "C_note"),
    //      (4, "D", 1, "D_note"),
    //      (5, "E", 1, "E_note")
    //    )
  )

  val setupFuture = db.run(setUpAction)
  Await.result(setupFuture, Duration.Inf)

  println("users:")
  val user = (2, "Jeff Dean", 1, "Google")
  val action = slickUsers.filter(_.id === 1).update(user)
  println(action.statements.mkString("|"))

  /**
  val f = db.run(slickUsers.result)
    .flatMap(res => {
      println(res)
      Future.successful(())
    })
  //  db.run(slickUsers.result.map(i => println(i)))
  //  db.run(slickUsers.map(_.user_name).result.map(println))
   */

  db.close()
}
